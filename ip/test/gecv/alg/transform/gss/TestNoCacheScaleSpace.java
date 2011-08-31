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

package gecv.alg.transform.gss;

import gecv.abst.filter.derivative.ImageGradient;
import gecv.alg.filter.blur.BlurImageOps;
import gecv.core.image.GeneralizedImageOps;
import gecv.core.image.ImageGenerator;
import gecv.core.image.inst.SingleBandGenerator;
import gecv.factory.filter.derivative.FactoryDerivative;
import gecv.factory.filter.kernel.FactoryKernelGaussian;
import gecv.struct.image.ImageFloat32;
import gecv.testing.GecvTesting;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class TestNoCacheScaleSpace {

	Random rand = new Random(234);
	int width = 20;
	int height = 30;

	ImageGenerator<ImageFloat32> generator = new SingleBandGenerator<ImageFloat32>(ImageFloat32.class);

	ImageFloat32 original = new ImageFloat32(width,height);

	@Before
	public void setup() {
		GeneralizedImageOps.randomize(original,rand,0,40);
	}

	@Test
	public void getScaledImage() {
		NoCacheScaleSpace<ImageFloat32,ImageFloat32> alg =
				new NoCacheScaleSpace<ImageFloat32,ImageFloat32>(generator,generator);

		int radius = FactoryKernelGaussian.radiusForSigma(1.2,0);
		ImageFloat32 expected = BlurImageOps.gaussian(original,null,1.2,radius,null);

		alg.setScales(1.2,2.3,3.5);
		alg.setImage(original);
		alg.setActiveScale(0);
		ImageFloat32 found = alg.getScaledImage();

		GecvTesting.assertEquals(expected,found,0,1e-4);
	}

	@Test
	public void getDerivative() {
		NoCacheScaleSpace<ImageFloat32,ImageFloat32> alg =
				new NoCacheScaleSpace<ImageFloat32,ImageFloat32>(generator,generator);

		double target = 2.3;

		ImageGradient<ImageFloat32,ImageFloat32> g =  FactoryDerivative.three_F32();

		ImageFloat32 derivX = new ImageFloat32(width,height);
		ImageFloat32 derivY = new ImageFloat32(width,height);
		ImageFloat32 derivXX = new ImageFloat32(width,height);
		ImageFloat32 derivYY = new ImageFloat32(width,height);
		ImageFloat32 derivXY = new ImageFloat32(width,height);
		ImageFloat32 derivYX = new ImageFloat32(width,height);
		ImageFloat32 derivYYX = new ImageFloat32(width,height);
		ImageFloat32 derivYYY = new ImageFloat32(width,height);

		alg.setScales(1.2,target,3.5);
		alg.setImage(original);
		alg.setActiveScale(1);

		g.process(alg.getScaledImage(),derivX,derivY);
		g.process(derivX,derivXX,derivXY);
		g.process(derivY,derivYX,derivYY);
		g.process(derivYY,derivYYX,derivYYY);


		// do one out of order which will force it to meet all the dependencies
		GecvTesting.assertEquals(derivYYY,alg.getDerivative(false,false,false),0,1e-4);
		GecvTesting.assertEquals(derivX,alg.getDerivative(true),0,1e-4);
		GecvTesting.assertEquals(derivY,alg.getDerivative(false),0,1e-4);
		GecvTesting.assertEquals(derivXX,alg.getDerivative(true,true),0,1e-4);
		GecvTesting.assertEquals(derivXY,alg.getDerivative(true,false),0,1e-4);
		GecvTesting.assertEquals(derivYY,alg.getDerivative(false,false),0,1e-4);
		GecvTesting.assertEquals(derivYYX,alg.getDerivative(false,false,true),0,1e-4);
	}
}