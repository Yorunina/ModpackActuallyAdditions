#version 330

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 ProjectionMatrix;
uniform mat4 ViewMatrix;
uniform vec2 OutSize;

in vec2 texCoord;
in vec4 near_4;
in vec4 far_4;

out vec4 fragColor;

#define INNER_RADIUS 4.0
#define OUTER_RADIUS 10.0
#define iterations 10
#define formuparam 0.56
#define volsteps 8
#define stepsize 0.1
#define brightness 0.0012
#define darkmatter 0.35
#define distfading 0.730
#define saturation 0.850

vec4 sphereFront(vec3 ro, vec3 rd, float r) {
    float b = dot(rd, ro);
    float c = dot(ro, ro) - r * r;
    float h = b * b - c;
    if (h < 0.0) {
        return vec4(0.0, 0.0, 0.0, -1.0);
    }
    float t = -b - sqrt(h);
    if (t < 0.0) {
        return vec4(0.0, 0.0, 0.0, -1.0);
    }
    return vec4(normalize(ro + rd * t), t);
}

bool occluded(vec3 p, float sceneDepth) {
    vec4 clip = ProjectionMatrix * ViewMatrix * vec4(p, 1.0);
    if (clip.w <= 0.0) {
        return true;
    }
    float depth = clip.z / clip.w * 0.5 + 0.5;
    return depth > sceneDepth + 1.0e-4;
}

vec3 starfield(vec3 rd) {
    vec3 from = vec3(0.8, 0.4, 0.9);
    float s = 0.1;
    float fade = 1.0;
    vec3 v = vec3(0.0);
    for (int r = 0; r < volsteps; r++) {
        vec3 p = from + s * rd * 0.5;
        p = abs(p) / max(dot(p, p), 0.0016) - formuparam;
        float pa = 0.0;
        float a = 0.0;
        for (int i = 0; i < iterations; i++) {
            p = abs(p) / dot(p, p) - formuparam;
            a += abs(length(p) - pa);
            pa = length(p);
        }
        float dm = max(0.0, darkmatter - a * a * 0.001);
        a *= a * a;
        if (r > 6) fade *= 1.0 - dm;
        v += fade;
        v += vec3(s, s * s, s * s * s * s) * a * brightness * fade;
        fade *= distfading;
        s += stepsize;
    }
    v = mix(vec3(length(v)), v, saturation);
    return v * 0.01;
}

vec3 lensing(vec3 col, vec3 ro, vec3 rd) {
    vec4 clip = ProjectionMatrix * ViewMatrix * vec4(0.0, 0.0, 0.0, 1.0);
    if (abs(clip.w) < 1.0e-4) {
        return col;
    }
    vec2 center = clip.xy / clip.w * 0.5 + 0.5;
    vec2 dir = texCoord - center;
    float dirLen = length(dir);
    if (dirLen < 1.0e-4) {
        return col;
    }
    float impact = length(cross(ro, rd));
    float warp = 1.0 - smoothstep(INNER_RADIUS, OUTER_RADIUS, impact);
    return texture(DiffuseSampler, texCoord - dir / dirLen * warp * 0.2).rgb;
}

vec3 render(float sceneDepth, vec3 col, vec3 ro, vec3 rd) {
    if (dot(ro, ro) < INNER_RADIUS * INNER_RADIUS) {
        return starfield(rd);
    }

    vec4 inner = sphereFront(ro, rd, INNER_RADIUS);
    if (inner.w >= 0.0 && !occluded(ro + rd * inner.w, sceneDepth)) {
        return starfield(rd);
    }

    vec4 outer = sphereFront(ro, rd, OUTER_RADIUS);
    if (outer.w >= 0.0 && !occluded(ro + rd * outer.w, sceneDepth)) {
        return lensing(col, ro, rd);
    }

    return col;
}

void main() {
    vec3 ro = near_4.xyz / near_4.w;
    vec3 rd = normalize(far_4.xyz / far_4.w - ro);
    vec2 uv = texCoord;
    uv.y = clamp(uv.y, 2.0 / OutSize.y, 1.0);
    vec3 color = render(texture(DepthSampler, texCoord).r, texture(DiffuseSampler, uv).rgb, ro, rd);
    fragColor = vec4(color, 1.0);
}
