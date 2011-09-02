/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.core.image.border;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author Peter Abeles
 */
public class TestBorderIndex1D_Extend {

	int length = 10;

	@Test
	public void simple() {
		BorderIndex1D_Extend alg = new BorderIndex1D_Extend();
		alg.setLength(length);

		for( int i = 0; i < 10; i++ ) {
			assertEquals(i,alg.getIndex(i));
		}

		assertEquals(0,alg.getIndex(-1));
		assertEquals(0,alg.getIndex(-2));
		assertEquals(9,alg.getIndex(length));
		assertEquals(9,alg.getIndex(length+1));
	}
}
