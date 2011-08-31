/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.transform.ii;

import gecv.alg.filter.convolve.ConvolveWithBorder;
import gecv.core.image.FactorySingleBandImage;
import gecv.core.image.GeneralizedImageOps;
import gecv.core.image.SingleBandImage;
import gecv.core.image.border.FactoryImageBorder;
import gecv.core.image.border.ImageBorder_F32;
import gecv.core.image.border.ImageBorder_I32;
import gecv.struct.ImageRectangle;
import gecv.struct.convolve.Kernel2D_F32;
import gecv.struct.convolve.Kernel2D_I32;
import gecv.struct.image.*;
import gecv.testing.GecvTesting;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import static junit.framework.Assert.assertEquals;


/**
 * @author Peter Abeles
 */
public class TestIntegralImageOps {

	Random rand = new Random(234);
	int width = 20;
	int height = 30;


	@Test
	public void transform() {
		int numFound = GecvTesting.findMethodThenCall(this,"transform",IntegralImageOps.class,"transform");
		assertEquals(3,numFound);
	}

	public void transform( Method m ) {
		Class<?> paramType[] = m.getParameterTypes();
		Class<?> inputType = paramType[0];
		Class<?> outputType = paramType[1];

		ImageBase input = GeneralizedImageOps.createImage(inputType,width,height);
		ImageBase integral = GeneralizedImageOps.createImage(outputType,width,height);

		GeneralizedImageOps.randomize(input,rand,0,100);

		GecvTesting.checkSubImage(this,"checkTransformResults",true,m,input,integral);
	}

	public void checkTransformResults(Method m , ImageBase a, ImageBase b) throws InvocationTargetException, IllegalAccessException {

		m.invoke(null,a,b);

		SingleBandImage aa = FactorySingleBandImage.wrap(a);
		SingleBandImage bb = FactorySingleBandImage.wrap(b);

		for( int y = 0; y < height; y++ ) {
			for( int x = 0; x < width; x++ ) {
				double total = 0;

				for( int i = 0; i <= y; i++ ) {
					for( int j = 0; j <= x; j++ ) {
						total += aa.get(j,i).doubleValue();
					}
				}

				Assert.assertEquals(x+" "+y,total,bb.get(x,y).doubleValue(),1e-1);
			}
		}
	}

	@Test
	public void convolve() {
		int numFound = GecvTesting.findMethodThenCall(this,"convolve",IntegralImageOps.class,"convolve");
		assertEquals(2,numFound);
	}

	public void convolve( Method m ) throws InvocationTargetException, IllegalAccessException {
		Class<?> paramType[] = m.getParameterTypes();
		Class<?> inputType = paramType[0];
		Class<?> outputType = paramType[2];
		Class<?> origType = GeneralizedImageOps.isFloatingPoint(inputType) ? ImageFloat32.class : ImageUInt8.class;

		ImageBase input = GeneralizedImageOps.createImage(origType,width,height);
		ImageBase integral = GeneralizedImageOps.createImage(outputType,width,height);

		GeneralizedImageOps.randomize(input,rand,0,10);
		GIntegralImageOps.transform(input,integral);

		ImageBase expected = GeneralizedImageOps.createImage(outputType,width,height);
		ImageBase found = GeneralizedImageOps.createImage(outputType,width,height);

		if( paramType[0] == ImageFloat32.class ) {
			Kernel2D_F32 kernel = new Kernel2D_F32(3, new float[]{1,1,1,2,2,2,1,1,1});
			ImageBorder_F32 border = FactoryImageBorder.value((ImageFloat32)input,0);
			ConvolveWithBorder.convolve(kernel,(ImageFloat32)input,(ImageFloat32)expected,border);
		} else {
			Kernel2D_I32 kernel = new Kernel2D_I32(new int[]{1,1,1,2,2,2,1,1,1},3);
			ImageBorder_I32 border = FactoryImageBorder.value((ImageInteger)input,0);
			ConvolveWithBorder.convolve(kernel,(ImageUInt8)input,(ImageSInt32)expected,border);
		}

		IntegralKernel kernel = new IntegralKernel(2);
		kernel.blocks[0] = new ImageRectangle(-2,-2,1,1);
		kernel.blocks[1] = new ImageRectangle(-2,-1,1,0);
		kernel.scales = new int[]{1,1};

		m.invoke(null,integral,kernel,found);

		GecvTesting.assertEqualsGeneric(expected,found,0,1e-4f);
	}


	@Test
	public void convolveBorder() {
		int numFound = GecvTesting.findMethodThenCall(this,"convolveBorder",IntegralImageOps.class,"convolveBorder");
		assertEquals(2,numFound);
	}

	public void convolveBorder( Method m ) throws InvocationTargetException, IllegalAccessException {
		Class<?> paramType[] = m.getParameterTypes();
		Class<?> inputType = paramType[0];
		Class<?> outputType = paramType[2];
		Class<?> origType = GeneralizedImageOps.isFloatingPoint(inputType) ? ImageFloat32.class : ImageUInt8.class;

		ImageBase input = GeneralizedImageOps.createImage(origType,width,height);
		ImageBase integral = GeneralizedImageOps.createImage(outputType,width,height);

		GeneralizedImageOps.randomize(input,rand,0,10);
		GIntegralImageOps.transform(input,integral);

		ImageBase expected = GeneralizedImageOps.createImage(outputType,width,height);
		ImageBase found = GeneralizedImageOps.createImage(outputType,width,height);

		if( paramType[0] == ImageFloat32.class ) {
			Kernel2D_F32 kernel = new Kernel2D_F32(3, new float[]{1,1,1,2,2,2,1,1,1});
			ImageBorder_F32 border = FactoryImageBorder.value((ImageFloat32)input,0);
			ConvolveWithBorder.convolve(kernel,(ImageFloat32)input,(ImageFloat32)expected,border);
		} else {
			Kernel2D_I32 kernel = new Kernel2D_I32(new int[]{1,1,1,2,2,2,1,1,1},3);
			ImageBorder_I32 border = FactoryImageBorder.value((ImageInteger)input,0);
			ConvolveWithBorder.convolve(kernel,(ImageUInt8)input,(ImageSInt32)expected,border);
		}

		IntegralKernel kernel = new IntegralKernel(2);
		kernel.blocks[0] = new ImageRectangle(-2,-2,1,1);
		kernel.blocks[1] = new ImageRectangle(-2,-1,1,0);
		kernel.scales = new int[]{1,1};

		m.invoke(null,integral,kernel,found,4,5);

		GecvTesting.assertEqualsBorder(expected,found,1e-4f,4,5);
	}

	@Test
	public void convolveSparse() {
		int numFound = GecvTesting.findMethodThenCall(this,"convolveSparse",IntegralImageOps.class,"convolveSparse");
		assertEquals(2,numFound);
	}

	public void convolveSparse( Method m ) throws InvocationTargetException, IllegalAccessException {
		Class<?> paramType[] = m.getParameterTypes();
		Class<?> inputType = paramType[0];

		ImageBase integral = GeneralizedImageOps.createImage(inputType,width,height);

		GeneralizedImageOps.randomize(integral,rand,0,1000);

		ImageBase expected = GeneralizedImageOps.createImage(inputType,width,height);

		IntegralKernel kernel = new IntegralKernel(2);
		kernel.blocks[0] = new ImageRectangle(-2,-2,1,1);
		kernel.blocks[1] = new ImageRectangle(-2,-1,1,0);
		kernel.scales =  new int[]{1,2};

		GIntegralImageOps.convolve(integral,kernel,expected);

		SingleBandImage e = FactorySingleBandImage.wrap(expected);

		double found0 = ((Number)m.invoke(null,integral,kernel,0,0)).doubleValue();
		double found1 = ((Number)m.invoke(null,integral,kernel,10,12)).doubleValue();
		double found2 = ((Number)m.invoke(null,integral,kernel,19,29)).doubleValue();

		assertEquals(e.get(0,0).doubleValue(),found0,1e-4f);
		assertEquals(e.get(10,12).doubleValue(),found1,1e-4f);
		assertEquals(e.get(19,29).doubleValue(),found2,1e-4f);
	}

	@Test
	public void block_unsafe() {
		int numFound = GecvTesting.findMethodThenCall(this,"block_unsafe",IntegralImageOps.class,"block_unsafe");
		assertEquals(2,numFound);
	}
	
	public void block_unsafe( Method m ) throws InvocationTargetException, IllegalAccessException {
		Class<?> paramType[] = m.getParameterTypes();
		Class<?> inputType = paramType[0];
		Class<?> origType = GeneralizedImageOps.isFloatingPoint(inputType) ? ImageFloat32.class : ImageUInt8.class;

		ImageBase input = GeneralizedImageOps.createImage(origType,width,height);
		ImageBase integral = GeneralizedImageOps.createImage(inputType,width,height);

		GeneralizedImageOps.fill(input,1);
		GIntegralImageOps.transform(input,integral);

		double found0 = ((Number)m.invoke(null,integral,4,5,8,8)).doubleValue();

		assertEquals(12,found0,1e-4f);
	}

	@Test
	public void block_zero() {
		int numFound = GecvTesting.findMethodThenCall(this,"block_zero",IntegralImageOps.class,"block_zero");
		assertEquals(2,numFound);
	}

	public void block_zero( Method m ) throws InvocationTargetException, IllegalAccessException {
		Class<?> paramType[] = m.getParameterTypes();
		Class<?> inputType = paramType[0];
		Class<?> origType = GeneralizedImageOps.isFloatingPoint(inputType) ? ImageFloat32.class : ImageUInt8.class;

		ImageBase input = GeneralizedImageOps.createImage(origType,width,height);
		ImageBase integral = GeneralizedImageOps.createImage(inputType,width,height);

		GeneralizedImageOps.fill(input,1);
		GIntegralImageOps.transform(input,integral);

		double found = ((Number)m.invoke(null,integral,4,5,8,8)).doubleValue();
		assertEquals(12,found,1e-4f);

		found = ((Number)m.invoke(null,integral,-1,-2,2,3)).doubleValue();
		assertEquals(12,found,1e-4f);

		found = ((Number)m.invoke(null,integral,width-2,height-3,width+1,height+3)).doubleValue();
		assertEquals(2,found,1e-4f);

		found = ((Number)m.invoke(null,integral,3,-4,-1,-1)).doubleValue();
		assertEquals(0,found,1e-4f);

		found = ((Number)m.invoke(null,integral,width+1,height+2,width+6,height+8)).doubleValue();
		assertEquals(0,found,1e-4f);
	}
}