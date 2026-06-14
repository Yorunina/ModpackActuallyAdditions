package net.yorunina.maa.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.yorunina.maa.items.BaseSelectorItem;
import net.yorunina.maa.networks.SyncSelectorEntryMessage;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class SelectorScreen extends Screen {

    private static final int ENTRY_HEIGHT = 14;
    private static final int SCROLL_BAR_WIDTH = 6;
    private static final int PADDING = 8;
    private static final int SEARCH_BOX_HEIGHT = 20;
    private static final int HEADER_HEIGHT = 40;

    private final BaseSelectorItem selectorItem;
    private final ItemStack itemStack;
    private final int slotIndex;
    private final boolean isOffHand;

    private EditBox searchBox;
    private final List<ResourceLocation> allEntries;
    private List<ResourceLocation> filteredEntries;
    private Map<String, List<ResourceLocation>> groupedEntries;
    private List<String> sortedGroups;

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int listTop;
    private int listHeight;
    private int listWidth;
    private int listLeft;

    private ResourceLocation selectedEntry;
    private int hoveredEntryIndex = -1;
    private String expandedGroup = null;
    private boolean isDraggingScrollBar = false;

    public SelectorScreen(BaseSelectorItem selectorItem, ItemStack itemStack, int slotIndex, boolean isOffHand) {
        super(selectorItem.getSelectorTypeName());
        this.selectorItem = selectorItem;
        this.itemStack = itemStack;
        this.slotIndex = slotIndex;
        this.isOffHand = isOffHand;
        this.allEntries = selectorItem.getEntriesForDisplay();
        this.filteredEntries = new ArrayList<>(allEntries);
        this.selectedEntry = selectorItem.getSelectedEntry(itemStack);
        groupEntries();
    }

    @Override
    protected void init() {
        super.init();

        listLeft = PADDING;
        listWidth = this.width - PADDING * 2;
        listTop = HEADER_HEIGHT + PADDING;
        listHeight = this.height - listTop - PADDING;

        searchBox = new EditBox(this.font, listLeft, PADDING, listWidth - 80, SEARCH_BOX_HEIGHT, Component.translatable("maa.screen.selector.search"));
        searchBox.setHint(Component.translatable("maa.screen.selector.search_hint"));
        searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(searchBox);

        updateMaxScroll();
    }

    private void groupEntries() {
        groupedEntries = new LinkedHashMap<>();
        for (ResourceLocation entry : filteredEntries) {
            String namespace = entry.getNamespace();
            groupedEntries.computeIfAbsent(namespace, k -> new ArrayList<>()).add(entry);
        }
        sortedGroups = new ArrayList<>(groupedEntries.keySet());
        Collections.sort(sortedGroups);
    }

    private void onSearchChanged(String searchText) {
        String search = searchText.toLowerCase(Locale.ROOT).trim();
        if (search.isEmpty()) {
            filteredEntries = new ArrayList<>(allEntries);
        } else {
            filteredEntries = allEntries.stream()
                    .filter(entry -> {
                        String path = entry.getPath().toLowerCase(Locale.ROOT);
                        String namespace = entry.getNamespace().toLowerCase(Locale.ROOT);
                        String displayName = selectorItem.getEntryDisplayName(entry).getString().toLowerCase(Locale.ROOT);
                        return path.contains(search) || namespace.contains(search) || displayName.contains(search);
                    })
                    .collect(Collectors.toList());
        }
        groupEntries();
        scrollOffset = 0;
        hoveredEntryIndex = -1;
        if (!groupedEntries.containsKey(expandedGroup)) {
            expandedGroup = null;
        }
        updateMaxScroll();
    }

    private void updateMaxScroll() {
        int totalHeight = calculateTotalHeight();
        maxScroll = Math.max(0, totalHeight - listHeight + PADDING);
    }

    private int calculateTotalHeight() {
        int height = 0;
        for (String group : sortedGroups) {
            height += ENTRY_HEIGHT;
            if (group.equals(expandedGroup)) {
                height += groupedEntries.get(group).size() * ENTRY_HEIGHT;
            }
        }
        return height;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        graphics.fillGradient(listLeft, listTop, listLeft + listWidth, listTop + listHeight, 0xC0101010, 0xD0101010);

        if (allEntries.isEmpty()) {
            Component emptyText = Component.translatable("maa.screen.selector.empty");
            graphics.drawCenteredString(font, emptyText, this.width / 2, listTop + listHeight / 2 - 10, 0xFF5555);
        } else {
            renderList(graphics, mouseX, mouseY);
            renderScrollBar(graphics);
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        String countText = Component.translatable("maa.screen.selector.count", filteredEntries.size(), allEntries.size()).getString();
        graphics.drawString(font, countText, this.width - font.width(countText) - PADDING, PADDING + 6, 0xAAAAAA, false);
    }

    private void renderList(GuiGraphics graphics, int mouseX, int mouseY) {
        int y = listTop - scrollOffset;
        hoveredEntryIndex = -1;
        int entryIndex = 0;

        graphics.enableScissor(listLeft, listTop, listLeft + listWidth - SCROLL_BAR_WIDTH - 2, listTop + listHeight);

        for (String group : sortedGroups) {
            if (y > listTop + listHeight) break;
            if (y + ENTRY_HEIGHT >= listTop) {
                boolean isHovered = mouseX >= listLeft && mouseX < listLeft + listWidth && mouseY >= y && mouseY < y + ENTRY_HEIGHT;
                boolean isExpanded = group.equals(expandedGroup);

                int bgColor = isHovered ? 0x40FFFFFF : 0x20FFFFFF;
                graphics.fill(listLeft, y, listLeft + listWidth - SCROLL_BAR_WIDTH - 2, y + ENTRY_HEIGHT, bgColor);

                String groupText = (isExpanded ? "▼ " : "▶ ") + group + " (" + groupedEntries.get(group).size() + ")";
                graphics.drawString(font, groupText, listLeft + 4, y + 3, 0xFFFFAA, false);
            }
            y += ENTRY_HEIGHT;
            entryIndex++;

            if (group.equals(expandedGroup)) {
                for (ResourceLocation entry : groupedEntries.get(group)) {
                    if (y > listTop + listHeight) break;
                    if (y + ENTRY_HEIGHT >= listTop) {
                        boolean isSelected = entry.equals(selectedEntry);
                        boolean isHovered = mouseX >= listLeft && mouseX < listLeft + listWidth - SCROLL_BAR_WIDTH - 2 && mouseY >= y && mouseY < y + ENTRY_HEIGHT;

                        if (isSelected) {
                            graphics.fill(listLeft, y, listLeft + listWidth - SCROLL_BAR_WIDTH - 2, y + ENTRY_HEIGHT, 0x8000AA00);
                        } else if (isHovered) {
                            graphics.fill(listLeft, y, listLeft + listWidth - SCROLL_BAR_WIDTH - 2, y + ENTRY_HEIGHT, 0x40FFFFFF);
                            hoveredEntryIndex = entryIndex;
                        }

                        Component displayName = selectorItem.getEntryDisplayName(entry);
                        graphics.drawString(font, displayName, listLeft + 16, y + 3, isSelected ? 0x55FF55 : 0xFFFFFF, false);

                        String pathText = "§8" + entry.getPath();
                        graphics.drawString(font, pathText, listLeft + listWidth - SCROLL_BAR_WIDTH - font.width(entry.getPath()) - 20, y + 3, 0xAAAAAA, false);
                    }
                    y += ENTRY_HEIGHT;
                    entryIndex++;
                }
            }
        }

        graphics.disableScissor();
    }

    private void renderScrollBar(GuiGraphics graphics) {
        if (maxScroll <= 0) return;

        int scrollBarHeight = (int) ((float) listHeight / (listHeight + maxScroll) * listHeight);
        scrollBarHeight = Math.max(20, scrollBarHeight);
        int scrollBarTop = listTop + (int) ((float) scrollOffset / maxScroll * (listHeight - scrollBarHeight));

        int scrollBarLeft = listLeft + listWidth - SCROLL_BAR_WIDTH;
        graphics.fill(scrollBarLeft, listTop, scrollBarLeft + SCROLL_BAR_WIDTH, listTop + listHeight, 0x80000000);
        graphics.fill(scrollBarLeft, scrollBarTop, scrollBarLeft + SCROLL_BAR_WIDTH, scrollBarTop + scrollBarHeight, 0xC0AAAAAA);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (maxScroll > 0 && mouseX >= listLeft + listWidth - SCROLL_BAR_WIDTH && mouseX < listLeft + listWidth && mouseY >= listTop && mouseY < listTop + listHeight) {
                isDraggingScrollBar = true;
                return true;
            }
            if (mouseX >= listLeft && mouseX < listLeft + listWidth - SCROLL_BAR_WIDTH - 2 && mouseY >= listTop && mouseY < listTop + listHeight) {
                int clickedIndex = getEntryAtPosition((int) mouseX, (int) mouseY);
                if (clickedIndex >= 0) {
                    ClickResult result = getClickResult(clickedIndex);
                    if (result != null) {
                        if (result.isGroup) {
                            if (expandedGroup != null && expandedGroup.equals(result.group)) {
                                expandedGroup = null;
                            } else {
                                String previousExpanded = expandedGroup;
                                expandedGroup = result.group;
                                adjustScrollForExpandedGroup(result.group, previousExpanded);
                            }
                            updateMaxScroll();
                        } else {
                            selectEntry(result.entry);
                            return true;
                        }
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDraggingScrollBar) {
            isDraggingScrollBar = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDraggingScrollBar && button == 0 && maxScroll > 0) {
            int scrollBarHeight = Math.max(20, (int) ((float) listHeight / (listHeight + maxScroll) * listHeight));
            double relativeY = mouseY - listTop - scrollBarHeight / 2.0;
            double scrollRatio = relativeY / (listHeight - scrollBarHeight);
            scrollOffset = Mth.clamp((int) (scrollRatio * maxScroll), 0, maxScroll);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void adjustScrollForExpandedGroup(String group, String previousExpanded) {
        int groupOffset = 0;
        
        for (String g : sortedGroups) {
            if (g.equals(group)) {
                break;
            }
            groupOffset += ENTRY_HEIGHT;
            if (g.equals(previousExpanded)) {
                groupOffset += groupedEntries.get(g).size() * ENTRY_HEIGHT;
            }
        }

        int groupSize = groupedEntries.get(group).size();
        int groupEndOffset = groupOffset + ENTRY_HEIGHT + groupSize * ENTRY_HEIGHT;

        if (groupEndOffset > scrollOffset + listHeight) {
            scrollOffset = Math.min(groupEndOffset - listHeight, groupOffset);
        }
        
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);
    }

    private static class ClickResult {
        final boolean isGroup;
        final String group;
        final ResourceLocation entry;

        ClickResult(String group) {
            this.isGroup = true;
            this.group = group;
            this.entry = null;
        }

        ClickResult(ResourceLocation entry) {
            this.isGroup = false;
            this.group = null;
            this.entry = entry;
        }
    }

    @Nullable
    private ClickResult getClickResult(int clickedIndex) {
        int index = 0;
        for (String group : sortedGroups) {
            if (index == clickedIndex) {
                return new ClickResult(group);
            }
            index++;
            if (group.equals(expandedGroup)) {
                List<ResourceLocation> entries = groupedEntries.get(group);
                if (clickedIndex < index + entries.size()) {
                    return new ClickResult(entries.get(clickedIndex - index));
                }
                index += entries.size();
            }
        }
        return null;
    }

    private int getEntryAtPosition(int mouseX, int mouseY) {
        int y = listTop - scrollOffset;
        int index = 0;

        for (String group : sortedGroups) {
            if (mouseY >= y && mouseY < y + ENTRY_HEIGHT) {
                return index;
            }
            y += ENTRY_HEIGHT;
            index++;

            if (group.equals(expandedGroup)) {
                List<ResourceLocation> entries = groupedEntries.get(group);
                for (int i = 0; i < entries.size(); i++) {
                    if (mouseY >= y && mouseY < y + ENTRY_HEIGHT) {
                        return index;
                    }
                    y += ENTRY_HEIGHT;
                    index++;
                }
            }
        }
        return -1;
    }

    private void selectEntry(ResourceLocation entry) {
        selectedEntry = entry;
        selectorItem.setSelectedEntry(itemStack, entry);
        new SyncSelectorEntryMessage(slotIndex, isOffHand, entry).sendToServer();
        this.onClose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= listLeft && mouseX < listLeft + listWidth && mouseY >= listTop && mouseY < listTop + listHeight) {
            scrollOffset = Mth.clamp(scrollOffset - (int) (delta * ENTRY_HEIGHT * 2), 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (hoveredEntryIndex >= 0) {
                ClickResult result = getClickResult(hoveredEntryIndex);
                if (result != null && !result.isGroup) {
                    selectEntry(result.entry);
                    return true;
                }
            }
        }
        if (searchBox.isFocused() && keyCode == GLFW.GLFW_KEY_DOWN) {
            navigateList(1);
            return true;
        }
        if (searchBox.isFocused() && keyCode == GLFW.GLFW_KEY_UP) {
            navigateList(-1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void navigateList(int direction) {
        int totalEntries = 0;
        for (String group : sortedGroups) {
            totalEntries++;
            if (group.equals(expandedGroup)) {
                totalEntries += groupedEntries.get(group).size();
            }
        }

        if (totalEntries == 0) return;

        if (hoveredEntryIndex < 0) {
            hoveredEntryIndex = direction > 0 ? 0 : totalEntries - 1;
        } else {
            hoveredEntryIndex = Mth.clamp(hoveredEntryIndex + direction, 0, totalEntries - 1);
        }

        int entryOffset = 0;
        int index = 0;
        for (String group : sortedGroups) {
            if (index == hoveredEntryIndex) {
                break;
            }
            entryOffset += ENTRY_HEIGHT;
            index++;
            if (group.equals(expandedGroup)) {
                int groupSize = groupedEntries.get(group).size();
                if (index + groupSize > hoveredEntryIndex) {
                    entryOffset += (hoveredEntryIndex - index) * ENTRY_HEIGHT;
                    break;
                }
                entryOffset += groupSize * ENTRY_HEIGHT;
                index += groupSize;
            }
        }

        int visibleTop = scrollOffset;
        int visibleBottom = scrollOffset + listHeight;

        if (entryOffset < visibleTop) {
            scrollOffset = entryOffset;
        } else if (entryOffset + ENTRY_HEIGHT > visibleBottom) {
            scrollOffset = entryOffset + ENTRY_HEIGHT - listHeight;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}