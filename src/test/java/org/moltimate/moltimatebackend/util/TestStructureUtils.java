package org.moltimate.moltimatebackend.util;

import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Bond;
import org.biojava.nbio.structure.Element;
import org.biojava.nbio.structure.Group;
import org.junit.Assert;
import org.junit.Test;

import javax.vecmath.Point3d;
import java.util.List;

public class TestStructureUtils {

	@Test
	public void testRMSD() {
		Atom atom1 = new Atom() {
			@Override
			public void setName(String s) {

			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public void setElement(Element element) {

			}

			@Override
			public Element getElement() {
				return null;
			}

			@Override
			public void setPDBserial(int i) {

			}

			@Override
			public int getPDBserial() {
				return 0;
			}

			@Override
			public void setCoords(double[] doubles) {

			}

			@Override
			public double[] getCoords() {
				return new double[]{getX(), getY(), getZ()};
			}

			@Override
			public Point3d getCoordsAsPoint3d() {
				return null;
			}

			@Override
			public void setX(double v) {

			}

			@Override
			public void setY(double v) {

			}

			@Override
			public void setZ(double v) {

			}

			@Override
			public double getX() {
				return 0;
			}

			@Override
			public double getY() {
				return 0;
			}

			@Override
			public double getZ() {
				return 0;
			}

			@Override
			public void setAltLoc(Character character) {

			}

			@Override
			public Character getAltLoc() {
				return null;
			}

			@Override
			public void setOccupancy(float v) {

			}

			@Override
			public float getOccupancy() {
				return 0;
			}

			@Override
			public void setTempFactor(float v) {

			}

			@Override
			public float getTempFactor() {
				return 0;
			}

			@Override
			public void setGroup(Group group) {

			}

			@Override
			public Group getGroup() {
				return null;
			}

			@Override
			public void addBond(Bond bond) {

			}

			@Override
			public List<Bond> getBonds() {
				return null;
			}

			@Override
			public void setBonds(List<Bond> list) {

			}

			@Override
			public boolean hasBond(Atom atom) {
				return false;
			}

			@Override
			public short getCharge() {
				return 0;
			}

			@Override
			public void setCharge(short i) {

			}

			@Override
			public String toPDB() {
				return null;
			}

			@Override
			public void toPDB(StringBuffer stringBuffer) {

			}

			@Override
			public Object clone() {
				return null;
			}
		};

		Atom atom2 = new Atom() {
			@Override
			public void setName(String s) {

			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public void setElement(Element element) {

			}

			@Override
			public Element getElement() {
				return null;
			}

			@Override
			public void setPDBserial(int i) {

			}

			@Override
			public int getPDBserial() {
				return 0;
			}

			@Override
			public void setCoords(double[] doubles) {

			}

			@Override
			public double[] getCoords() {
				return new double[]{getX(), getY(), getZ()};
			}

			@Override
			public Point3d getCoordsAsPoint3d() {
				return null;
			}

			@Override
			public void setX(double v) {

			}

			@Override
			public void setY(double v) {

			}

			@Override
			public void setZ(double v) {

			}

			@Override
			public double getX() {
				return 1;
			}

			@Override
			public double getY() {
				return 0;
			}

			@Override
			public double getZ() {
				return 0;
			}

			@Override
			public void setAltLoc(Character character) {

			}

			@Override
			public Character getAltLoc() {
				return null;
			}

			@Override
			public void setOccupancy(float v) {

			}

			@Override
			public float getOccupancy() {
				return 0;
			}

			@Override
			public void setTempFactor(float v) {

			}

			@Override
			public float getTempFactor() {
				return 0;
			}

			@Override
			public void setGroup(Group group) {

			}

			@Override
			public Group getGroup() {
				return null;
			}

			@Override
			public void addBond(Bond bond) {

			}

			@Override
			public List<Bond> getBonds() {
				return null;
			}

			@Override
			public void setBonds(List<Bond> list) {

			}

			@Override
			public boolean hasBond(Atom atom) {
				return false;
			}

			@Override
			public short getCharge() {
				return 0;
			}

			@Override
			public void setCharge(short i) {

			}

			@Override
			public String toPDB() {
				return null;
			}

			@Override
			public void toPDB(StringBuffer stringBuffer) {

			}

			@Override
			public Object clone() {
				return null;
			}
		};

		Assert.assertEquals( 1, StructureUtils.rmsd( atom1, atom2 ), 0.1 );
	}
}
