package toolbox;

import org.joml.Matrix4f;
import toolbox.Points.Point2D;

public class Vector2D {
    public float x, y;

    public Vector2D() {
        x = y = 0;
    }

    public Vector2D(final float v) {
        x = y = v;
    }

    public Vector2D(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(final Point2D p) {
        x = p.x;
        y = p.y;
    }

    public void set(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        return div(length());
    }

    public float dot(final Vector2D v) {
        return x * v.x + y * v.y;
    }

    public Vector2D rotate(final float a) {
        final float sin = (float) Math.sin(a);
        final float cos = (float) Math.cos(a);

        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    public Vector2D floor() {
        return new Vector2D((int) Math.floor(x), (int) Math.floor(y));
    }

    public Vector2D mod(final int v) {
        return new Vector2D(x % v, y % v);
    }

    public Vector2D add(final Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    public Vector2D add(final float v) {
        return new Vector2D(x + v, y + v);
    }

    public Vector2D add(final float x, final float y) {
        return new Vector2D(this.x + x, this.y + y);
    }

    public Vector2D sub(final Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    public Vector2D sub(final float v) {
        return new Vector2D(x - v, y - v);
    }

    public Vector2D sub(final float x, final float y) {
        return new Vector2D(this.x - x, this.y - y);
    }

    public Vector2D mult(final float v) {
        return new Vector2D(x * v, y * v);
    }

    public Vector2D mult(final float x, final float y) {
        return new Vector2D(this.x * x, this.y * y);
    }

    public Vector2D mult(final Vector2D v) {
        return new Vector2D(x * v.x, y * v.y);
    }

    public Vector2D mult(final Matrix4f m, final float z, final float w) {
        final Vector2D result = new Vector2D();

        result.x += m.m00() * x + m.m01() * y + m.m02() * z + m.m03() * w;
        result.y += m.m10() * x + m.m11() * y + m.m12() * z + m.m13() * w;

        return result;
    }


    public Vector2D div(final float v) {
        return new Vector2D(x / v, y / v);
    }

    public Vector2D div(final float x, final float y) {
        return new Vector2D(this.x / x, this.y / y);
    }

    public Vector2D div(final Vector2D v) {
        return new Vector2D(x / v.x, y / v.y);
    }

    public Point2D toPoint2D() {
        return new Point2D(x, y);
    }

    @Override
    public String toString() {
        return "(X:" + x + " Y:" + y + ")";
    }

    @Override
    public Vector2D clone() {
        return new Vector2D(x, y);
    }
}
