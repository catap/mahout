/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.matrix;

import junit.framework.TestCase;

public class TestMatrixView extends TestCase {

  private static final int ROW = AbstractMatrix.ROW;

  private static final int COL = AbstractMatrix.COL;

  private final double[][] values = { { 0.0, 1.1, 2.2 }, { 1.1, 2.2, 3.3 },
      { 3.3, 4.4, 5.5 }, { 5.5, 6.6, 7.7 }, { 7.7, 8.8, 9.9 } };

  private Matrix test;

  public TestMatrixView(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    int[] offset = { 1, 1 };
    int[] card = { 3, 2 };
    test = new MatrixView(new DenseMatrix(values), offset, card);
  }

  public void testAsFormatString() {
    assertEquals("format",
        "[, [, 2.2, 3.3, ], [, 4.4, 5.5, ], [, 6.6, 7.7, ], ] ", test
            .asWritableComparable().toString());
  }

  public void testCardinality() {
    int[] c = test.cardinality();
    assertEquals("row cardinality", values.length - 2, c[ROW]);
    assertEquals("col cardinality", values[0].length - 1, c[COL]);
  }

  public void testCopy() {
    int[] c = test.cardinality();
    Matrix copy = test.copy();
    assertTrue("wrong class", copy instanceof MatrixView);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            test.getQuick(row, col), copy.getQuick(row, col));
  }

  public void testGetQuick() {
    int[] c = test.cardinality();
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            values[row + 1][col + 1], test.getQuick(row, col));
  }

  public void testHaveSharedCells() {
    assertTrue("same", test.haveSharedCells(test));
    assertFalse("different", test.haveSharedCells(test.copy()));
  }

  public void testLike() {
    Matrix like = test.like();
    assertTrue("type", like instanceof DenseMatrix);
    assertEquals("rows", test.cardinality()[ROW], like.cardinality()[ROW]);
    assertEquals("columns", test.cardinality()[COL], like.cardinality()[COL]);
  }

  public void testLikeIntInt() {
    Matrix like = test.like(4, 4);
    assertTrue("type", like instanceof DenseMatrix);
    assertEquals("rows", 4, like.cardinality()[ROW]);
    assertEquals("columns", 4, like.cardinality()[COL]);
  }

  public void testSetQuick() {
    int[] c = test.cardinality();
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++) {
        test.setQuick(row, col, 1.23);
        assertEquals("value[" + row + "][" + col + ']', 1.23, test.getQuick(
            row, col));
      }
  }

  public void testSize() {
    int[] c = test.size();
    assertEquals("row size", values.length - 2, c[ROW]);
    assertEquals("col size", values[0].length - 1, c[COL]);
  }

  public void testToArray() {
    double[][] array = test.toArray();
    int[] c = test.cardinality();
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            values[row + 1][col + 1], array[row][col]);
  }

  public void testViewPart() throws Exception {
    int[] offset = { 1, 1 };
    int[] size = { 2, 1 };
    Matrix view = test.viewPart(offset, size);
    int[] c = view.cardinality();
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            values[row + 2][col + 2], view.getQuick(row, col));
  }

  public void testViewPartCardinality() {
    int[] offset = { 1, 1 };
    int[] size = { 3, 3 };
    try {
      test.viewPart(offset, size);
      fail("exception expected");
    } catch (CardinalityException e) {
      assertTrue(true);
    } catch (IndexException e) {
      fail("cardinality exception expected");
    }
  }

  public void testViewPartIndexOver() {
    int[] offset = { 1, 1 };
    int[] size = { 2, 2 };
    try {
      test.viewPart(offset, size);
      fail("exception expected");
    } catch (CardinalityException e) {
      fail("index exception expected");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

  public void testViewPartIndexUnder() {
    int[] offset = { -1, -1 };
    int[] size = { 2, 2 };
    try {
      test.viewPart(offset, size);
      fail("exception expected");
    } catch (CardinalityException e) {
      fail("index exception expected");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

  public void testAssignDouble() {
    int[] c = test.cardinality();
    test.assign(4.53);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']', 4.53, test.getQuick(
            row, col));
  }

  public void testAssignDoubleArrayArray() throws Exception {
    int[] c = test.cardinality();
    test.assign(new double[3][2]);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']', 0.0, test.getQuick(row,
            col));
  }

  public void testAssignDoubleArrayArrayCardinality() {
    int[] c = test.cardinality();
    try {
      test.assign(new double[c[ROW] + 1][c[COL]]);
      fail("exception expected");
    } catch (CardinalityException e) {
      assertTrue(true);
    }
  }

  public void testAssignMatrixBinaryFunction() throws Exception {
    int[] c = test.cardinality();
    test.assign(test, new PlusFunction());
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            2 * values[row + 1][col + 1], test.getQuick(row, col));
  }

  public void testAssignMatrixBinaryFunctionCardinality() {
    try {
      test.assign(test.transpose(), new PlusFunction());
      fail("exception expected");
    } catch (CardinalityException e) {
      assertTrue(true);
    }
  }

  public void testAssignMatrix() throws Exception {
    int[] c = test.cardinality();
    Matrix value = test.like();
    value.assign(test);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            test.getQuick(row, col), value.getQuick(row, col));
  }

  public void testAssignMatrixCardinality() {
    try {
      test.assign(test.transpose());
      fail("exception expected");
    } catch (CardinalityException e) {
      assertTrue(true);
    }
  }

  public void testAssignUnaryFunction() {
    int[] c = test.cardinality();
    test.assign(new NegateFunction());
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            -values[row + 1][col + 1], test.getQuick(row, col));
  }

  public void testDivide() {
    int[] c = test.cardinality();
    Matrix value = test.divide(4.53);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            values[row + 1][col + 1] / 4.53, value.getQuick(row, col));
  }

  public void testGet() throws Exception {
    int[] c = test.cardinality();
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            values[row + 1][col + 1], test.get(row, col));
  }

  public void testGetIndexUnder() {
    int[] c = test.cardinality();
    try {
      for (int row = -1; row < c[ROW]; row++)
        for (int col = 0; col < c[COL]; col++)
          test.get(row, col);
      fail("index exception expected");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

  public void testGetIndexOver() {
    int[] c = test.cardinality();
    try {
      for (int row = 0; row < c[ROW] + 1; row++)
        for (int col = 0; col < c[COL]; col++)
          test.get(row, col);
      fail("index exception expected");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

  public void testMinus() throws Exception {
    int[] c = test.cardinality();
    Matrix value = test.minus(test);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']', 0.0, value.getQuick(
            row, col));
  }

  public void testMinusCardinality() {
    try {
      test.minus(test.transpose());
      fail("cardinality exception expected");
    } catch (CardinalityException e) {
      assertTrue(true);
    }
  }

  public void testPlusDouble() {
    int[] c = test.cardinality();
    Matrix value = test.plus(4.53);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            values[row + 1][col + 1] + 4.53, value.getQuick(row, col));
  }

  public void testPlusMatrix() throws Exception {
    int[] c = test.cardinality();
    Matrix value = test.plus(test);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            values[row + 1][col + 1] * 2, value.getQuick(row, col));
  }

  public void testPlusMatrixCardinality() {
    try {
      test.plus(test.transpose());
      fail("cardinality exception expected");
    } catch (CardinalityException e) {
      assertTrue(true);
    }
  }

  public void testSetUnder() {
    int[] c = test.cardinality();
    try {
      for (int row = -1; row < c[ROW]; row++)
        for (int col = 0; col < c[COL]; col++) {
          test.set(row, col, 1.23);
        }
      fail("index exception expected");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

  public void testSetOver() {
    int[] c = test.cardinality();
    try {
      for (int row = 0; row < c[ROW] + 1; row++)
        for (int col = 0; col < c[COL]; col++) {
          test.set(row, col, 1.23);
        }
      fail("index exception expected");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

  public void testTimesDouble() {
    int[] c = test.cardinality();
    Matrix value = test.times(4.53);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            values[row + 1][col + 1] * 4.53, value.getQuick(row, col));
  }

  public void testTimesMatrix() throws Exception {
    int[] c = test.cardinality();
    Matrix transpose = test.transpose();
    Matrix value = test.times(transpose);
    int[] v = value.cardinality();
    assertEquals("rows", c[ROW], v[ROW]);
    assertEquals("cols", c[ROW], v[COL]);
    // TODO: check the math too, lazy
  }

  public void testTimesMatrixCardinality() {
    Matrix other = test.like(5, 8);
    try {
      test.times(other);
      fail("cardinality exception expected");
    } catch (CardinalityException e) {
      assertTrue(true);
    }
  }

  public void testTranspose() {
    int[] c = test.cardinality();
    Matrix transpose = test.transpose();
    int[] t = transpose.cardinality();
    assertEquals("rows", c[COL], t[ROW]);
    assertEquals("cols", c[ROW], t[COL]);
    for (int row = 0; row < c[ROW]; row++)
      for (int col = 0; col < c[COL]; col++)
        assertEquals("value[" + row + "][" + col + ']',
            test.getQuick(row, col), transpose.getQuick(col, row));
  }

  public void testZSum() {
    double sum = test.zSum();
    assertEquals("zsum", 29.7, sum);
  }

  public void testAssignRow() throws Exception {
    double[] data = { 2.1, 3.2 };
    test.assignRow(1, new DenseVector(data));
    assertEquals("test[1][0]", 2.1, test.getQuick(1, 0));
    assertEquals("test[1][1]", 3.2, test.getQuick(1, 1));
  }

  public void testAssignRowCardinality() {
    double[] data = { 2.1, 3.2, 4.3 };
    try {
      test.assignRow(1, new DenseVector(data));
      fail("expecting cardinality exception");
    } catch (CardinalityException e) {
      assertTrue(true);
    }
  }

  public void testAssignColumn() throws Exception {
    double[] data = { 2.1, 3.2, 4.3 };
    test.assignColumn(1, new DenseVector(data));
    assertEquals("test[0][1]", 2.1, test.getQuick(0, 1));
    assertEquals("test[1][1]", 3.2, test.getQuick(1, 1));
    assertEquals("test[2][1]", 4.3, test.getQuick(2, 1));
  }

  public void testAssignColumnCardinality() {
    double[] data = { 2.1, 3.2 };
    try {
      test.assignColumn(1, new DenseVector(data));
      fail("expecting cardinality exception");
    } catch (CardinalityException e) {
      assertTrue(true);
    }
  }

  public void testGetRow() throws Exception {
    Vector row = test.getRow(1);
    assertEquals("row size", 2, row.size());
  }

  public void testGetRowIndexUnder() {
    try {
      test.getRow(-1);
      fail("expecting index exception");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

  public void testGetRowIndexOver() {
    try {
      test.getRow(5);
      fail("expecting index exception");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

  public void testGetColumn() throws Exception {
    Vector column = test.getColumn(1);
    assertEquals("row size", 3, column.size());
  }

  public void testGetColumnIndexUnder() {
    try {
      test.getColumn(-1);
      fail("expecting index exception");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

  public void testGetColumnIndexOver() {
    try {
      test.getColumn(5);
      fail("expecting index exception");
    } catch (IndexException e) {
      assertTrue(true);
    }
  }

}
