package blue.made.turrem.terraintest;

import java.lang.Override;

public class Vector3d implements Vector {
  public double x;

  public double y;

  public double z;

  public Vector3d(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vector3d(Vector3d original) {
    this.copy(original);
  }

  public Vector3d(Vector original) {
    this.copy(original);
  }

  public Vector3d(double value) {
    this.x = value;
    this.y = value;
    this.z = value;
  }

  public Vector3d() {
    this(0);
  }

  @Override
  public int size() {
    return 3;
  }

  @Override
  public double get(int index) {
    switch(index) {
      case 0:
        return this.x;
      case 1:
        return this.y;
      case 2:
        return this.z;
      default:
        return 0;
    }
  }

  @Override
  public void set(int index, double value) {
    switch(index) {
      case 0:
        this.x = value;
        return;
      case 1:
        this.y = value;
        return;
      case 2:
        this.z = value;
        return;
    }
  }

  @Override
  public double lengthSqr() {
    double sqr;
    double sum = 0;
    sqr = this.x;
    sqr *= sqr;
    sum += sqr;
    sqr = this.y;
    sqr *= sqr;
    sum += sqr;
    sqr = this.z;
    sqr *= sqr;
    sum += sqr;
    return sum;
  }

  @Override
  public double[] values() {
    return new double[] { this.x, this.y, this.z };
  }

  @Override
  public Vector3d clone() {
    return new Vector3d(this);
  }

  @Override
  public void add(Vector other) {
    this.x += other.get(0);
    this.y += other.get(1);
    this.z += other.get(2);
  }

  public void add(Vector3d other) {
    this.x += other.x;
    this.y += other.y;
    this.z += other.z;
  }

  @Override
  public void add(double value) {
    this.x += value;
    this.y += value;
    this.z += value;
  }

  public static Vector3d add(Vector a, Vector b) {
    Vector3d out = new Vector3d(a);
    out.add(b);
    return out;
  }

  @Override
  public void subtract(Vector other) {
    this.x -= other.get(0);
    this.y -= other.get(1);
    this.z -= other.get(2);
  }

  public void subtract(Vector3d other) {
    this.x -= other.x;
    this.y -= other.y;
    this.z -= other.z;
  }

  @Override
  public void subtract(double value) {
    this.x -= value;
    this.y -= value;
    this.z -= value;
  }

  public static Vector3d subtract(Vector a, Vector b) {
    Vector3d out = new Vector3d(a);
    out.subtract(b);
    return out;
  }

  @Override
  public void multiply(Vector other) {
    this.x *= other.get(0);
    this.y *= other.get(1);
    this.z *= other.get(2);
  }

  public void multiply(Vector3d other) {
    this.x *= other.x;
    this.y *= other.y;
    this.z *= other.z;
  }

  @Override
  public void multiply(double value) {
    this.x *= value;
    this.y *= value;
    this.z *= value;
  }

  public static Vector3d multiply(Vector a, Vector b) {
    Vector3d out = new Vector3d(a);
    out.multiply(b);
    return out;
  }

  @Override
  public void divide(Vector other) {
    this.x /= other.get(0);
    this.y /= other.get(1);
    this.z /= other.get(2);
  }

  public void divide(Vector3d other) {
    this.x /= other.x;
    this.y /= other.y;
    this.z /= other.z;
  }

  @Override
  public void divide(double value) {
    this.x /= value;
    this.y /= value;
    this.z /= value;
  }

  public static Vector3d divide(Vector a, Vector b) {
    Vector3d out = new Vector3d(a);
    out.divide(b);
    return out;
  }

  @Override
  public void copy(Vector from) {
    this.x = from.get(0);
    this.y = from.get(1);
    this.z = from.get(2);
  }

  public void copy(Vector3d from) {
    this.x = from.x;
    this.y = from.y;
    this.z = from.z;
  }

  @Override
  public void fill(double value) {
    this.x = value;
    this.y = value;
    this.z = value;
  }
}
