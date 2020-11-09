package org.moltimate.moltimatebackend.validation;

import org.junit.Test;
import org.moltimate.moltimatebackend.constant.EcNumber;
import org.moltimate.moltimatebackend.exception.InvalidEcNumberException;

public class TestEcNumberValidator {
	@Test
	public void testValidEcNumber() {
		EcNumberValidator.validate( "123456" );
		EcNumberValidator.validate( "1.2.3.4.5.6" );
	}

	@Test( expected = InvalidEcNumberException.class )
	public void testInvalidEcNumber() {
		EcNumberValidator.validate( "12a3456" );
	}

	@Test( expected = InvalidEcNumberException.class )
	public void testInvalidEcNumberDecimals() {
		EcNumberValidator.validate( "1.2.3a.4.5.6" );
	}

	@Test(expected = InvalidEcNumberException.class)
	public void testUnknownEcNumber() {
		EcNumberValidator.validate(EcNumber.UNKNOWN);
	}
}
