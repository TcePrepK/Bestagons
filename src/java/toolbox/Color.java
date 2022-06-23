package toolbox;

import static core.GlobalVariables.rand;

public class Color {
    public final float r, g, b, a;

    public Color() {
        r = g = b = a = 0;
    }

    public Color(final float r, final float g, final float b, final float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(final float r, final float g, final float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        a = 1;
    }

    public static Color randomColor() {
        return new Color(rand.nextInt(256) / 256f, rand.nextInt(256) / 256f, rand.nextInt(256) / 256f, rand.nextInt(256) / 256f);
    }
}
