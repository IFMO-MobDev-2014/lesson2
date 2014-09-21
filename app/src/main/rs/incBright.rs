#pragma version(1)
#pragma rs java_package_name(ru.ifmo.md.lesson2)

rs_allocation inAlloc;
rs_allocation outAlloc;
rs_script gScript;

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
    int r = (int)(*v_in)[0], g = (int)(*v_in)[1], b = (int)(*v_in)[2];
    // RGB to HSB
    float hsb[3];
    int cmax = (r > g) ? r : g;
    if (b > cmax) cmax = b;
    int cmin = (r < g) ? r : g;
    if (b < cmin) cmin = b;

    hsb[2] = ((float) cmax) / 255.0f;
    if (cmax != 0)
        hsb[1] = ((float) (cmax - cmin)) / ((float) cmax);
    else
        hsb[1] = 0;
    if (hsb[1] == 0)
        hsb[0] = 0;
    else {
        float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
        float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
        float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
        if (r == cmax)
            hsb[0] = bluec - greenc;
        else if (g == cmax)
            hsb[0] = 2.0f + redc - bluec;
        else
            hsb[0] = 4.0f + greenc - redc;
        hsb[0] /= 6.0f;
        if (hsb[0] < 0)
            hsb[0] += 1.0f;
    }

    hsb[2] *= 2;

    // HSB to RGB
    if (hsb[1] == 0) {
        r = g = b = (int) (hsb[2] * 255.0f + 0.5f);
    } else {
        float h = (hsb[0] - (float)(int)hsb[0]) * 6.0f;
        float f = h - (float)(int)h;
        float p = hsb[2] * (1.0f - hsb[1]);
        float q = hsb[2] * (1.0f - hsb[1] * f);
        float t = hsb[2] * (1.0f - (hsb[1] * (1.0f - f)));
        switch ((int) h) {
            case 0:
                r = (int) (hsb[2] * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (hsb[2] * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (hsb[2] * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (hsb[2] * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (hsb[2] * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (hsb[2] * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
        }
    }
    r = min(r, 255);
    g = min(g, 255);
    b = min(b, 255);

    uchar4 color = {r, g, b, 255};
    *v_out = color;
}

void filter() {
    rsForEach(gScript, inAlloc, outAlloc);
}