package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * This is the test suite for TODO . It starts by testing to make sure all the
 * equals(), toString(), and hashCode() methods work correctly as they are the
 * foundation for the entire project. It ends by testing the more specific
 * methods for each class. Must supply valid inputs for the tests as defined by
 * the preconditions in each class to pass these tests.
 */

public class TODOTest {

	/*
	 * Testing strategy
	 * 
	 * Goal: make sure every TODO method works correctly
	 * 
	 * Strategy:
	 */

	/*
	 * Test TODO objects for the equals methods by TODO
	 */
	@Test
	public void equalsTODOTest() {

		// All equality tests will pass if reflexivity, symmetry, and
		// transitivity hold therefore the test for equality will assert these
		// are always true

		// reflexivity
		assertEquals(true, false);

		// symmetry
		assertEquals(true, false);

		// non-equal comparisons
		assertFalse(true == true);
	}

	/**
	 * Test all Fraction objects for the hashCode methods, testing that equal
	 * Fractions yield the same hashCode.
	 */
	@Test
	public void hashCodeTest() {
		
		assertTrue(this.hashCode() == this.hashCode());
	}

}