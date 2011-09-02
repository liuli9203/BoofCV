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

package boofcv.alg.interpolate.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;
import boofcv.misc.CodeGeneratorUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * @author Peter Abeles
 */
public class GenerateBilinearPixel extends CodeGeneratorBase {
	String className;

	PrintStream out;
	AutoTypeImage image;

	@Override
	public void generate() throws FileNotFoundException {
		createType(AutoTypeImage.F32);
		createType(AutoTypeImage.U8);
		createType(AutoTypeImage.S16);
		createType(AutoTypeImage.S32);
	}

	private void createType( AutoTypeImage type ) throws FileNotFoundException {
		className = "BilinearPixel_"+type.name();
		image = type;

		createFile();
	}

	private void createFile() throws FileNotFoundException {
		out = new PrintStream(new FileOutputStream(className + ".java"));
		printPreamble();
		printTheRest();
		out.println("}");
	}

	private void printPreamble() {
		out.print(CodeGeneratorUtil.copyright);
		out.print("package gecv.alg.interpolate.impl;\n");
		out.println();
		out.print("import gecv.alg.interpolate.InterpolatePixel;\n" +
				"import gecv.struct.image."+image.getImageName()+";\n");
		out.println();
		out.println();
		out.print("/**\n" +
				" * <p>\n" +
				" * Performs bilinear interpolation to extract values between pixels in an image.  When a boundary is encountered\n" +
				" * the number of pixels used to interpolate is automatically reduced.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * NOTE: This code was automatically generated using {@link GenerateBilinearPixel}.\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+className+" implements InterpolatePixel<"+image.getImageName()+"> {\n" +
				"\n" +
				"\tprivate "+image.getImageName()+" orig;\n" +
				"\n" +
				"\tprivate "+image.getDataType()+" data[];\n" +
				"\tprivate int stride;\n" +
				"\tprivate int width;\n" +
				"\tprivate int height;\n" +
				"\n" +
				"\tpublic "+className+"() {\n" +
				"\t}\n" +
				"\n" +
				"\tpublic "+className+"("+image.getImageName()+" orig) {\n" +
				"\t\tsetImage(orig);\n" +
				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic void setImage("+image.getImageName()+" image) {\n" +
				"\t\tthis.orig = image;\n" +
				"\t\tthis.data = orig.data;\n" +
				"\t\tthis.stride = orig.getStride();\n" +
				"\t\tthis.width = orig.getWidth();\n" +
				"\t\tthis.height = orig.getHeight();\n" +
				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic "+image.getImageName()+" getImage() {\n" +
				"\t\treturn orig;\n" +
				"\t}\n\n");

	}

	private void printTheRest() {
		String bitWise = image.getBitWise();

		out.print("\t@Override\n" +
				"\tpublic float get_unsafe(float x, float y) {\n" +
				"\t\tint xt = (int) x;\n" +
				"\t\tint yt = (int) y;\n" +
				"\t\tfloat ax = x - xt;\n" +
				"\t\tfloat ay = y - yt;\n" +
				"\n" +
				"\t\tint index = orig.startIndex + yt * stride + xt;\n" +
				"\n" +
				"\t\tint dx = xt == width - 1 ? 0 : 1;\n" +
				"\t\tint dy = yt == height - 1 ? 0 : stride;\n" +
				"\n" +
				"\t\tfloat val = (1.0f - ax) * (1.0f - ay) * (data[index] "+bitWise+"); // (x,y)\n" +
				"\t\tval += ax * (1.0f - ay) * (data[index + dx] "+bitWise+"); // (x+1,y)\n" +
				"\t\tval += ax * ay * (data[index + dx + dy] "+bitWise+"); // (x+1,y+1)\n" +
				"\t\tval += (1.0f - ax) * ay * (data[index + dy] "+bitWise+"); // (x,y+1)\n" +
				"\n" +
				"\t\treturn val;\n" +
				"\t}\n" +
				"\n" +
				"\t@Override\n" +
				"\tpublic float get(float x, float y) {\n" +
				"\t\tint xt = (int) x;\n" +
				"\t\tint yt = (int) y;\n" +
				"\n" +
				"\t\tif (xt < 0 || yt < 0 || xt >= width || yt >= height)\n" +
				"\t\t\tthrow new IllegalArgumentException(\"Point is outside of the image\");\n" +
				"\n" +
				"\t\tfloat ax = x - xt;\n" +
				"\t\tfloat ay = y - yt;\n" +
				"\n" +
				"\t\tint index = orig.startIndex + yt * stride + xt;\n" +
				"\n" +
				"\t\t// throw allows borders to be interpolated gracefully by double counting appropriate pixels\n" +
				"\t\tint dx = xt == width - 1 ? 0 : 1;\n" +
				"\t\tint dy = yt == height - 1 ? 0 : stride;\n" +
				"\n" +
				"\t\tfloat val = (1.0f - ax) * (1.0f - ay) * (data[index] "+bitWise+"); // (x,y)\n" +
				"\t\tval += ax * (1.0f - ay) * (data[index + dx] "+bitWise+"); // (x+1,y)\n" +
				"\t\tval += ax * ay * (data[index + dx + dy] "+bitWise+"); // (x+1,y+1)\n" +
				"\t\tval += (1.0f - ax) * ay * (data[index + dy] "+bitWise+"); // (x,y+1)\n" +
				"\n" +
				"\t\treturn val;\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateBilinearPixel gen = new GenerateBilinearPixel();
		gen.generate();
	}
}
