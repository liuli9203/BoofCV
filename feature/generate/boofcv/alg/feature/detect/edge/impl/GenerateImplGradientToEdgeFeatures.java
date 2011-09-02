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

package boofcv.alg.feature.detect.edge.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;
import boofcv.misc.CodeGeneratorUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * @author Peter Abeles
 */
public class GenerateImplGradientToEdgeFeatures extends CodeGeneratorBase {
	String className = "ImplGradientToEdgeFeatures";

	PrintStream out;

	public GenerateImplGradientToEdgeFeatures() throws FileNotFoundException {
		out = new PrintStream(new FileOutputStream(className + ".java"));
	}

	@Override
	public void generate() throws FileNotFoundException {
		printPreamble();

		printFunctions(AutoTypeImage.F32);
		printFunctions(AutoTypeImage.S16);
		printFunctions(AutoTypeImage.S32);

		out.print("\n" +
				"}\n");
	}

	private void printPreamble() {
		out.print(CodeGeneratorUtil.copyright);
		out.print("package gecv.alg.detect.edge.impl;\n" +
				"\n" +
				"import gecv.struct.image.*;\n" +
				"\n" +
				"/**\n" +
				" * <p>\n" +
				" * Implementations of the core algorithms of {@link gecv.alg.detect.edge.GradientToEdgeFeatures}.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * WARNING: Do not modify.  Automatically generated by {@link GenerateImplGradientToEdgeFeatures}.\n" +
				" * </p>\n" +
				" * \n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+className+" {\n\n");
	}

	private void printFunctions(AutoTypeImage derivType )
	{
		printItensityE(derivType);
		printIntensityAbs(derivType);
		printDirection(derivType);
	}

	private void printItensityE(AutoTypeImage derivType) {

		String bitWise = derivType.getBitWise();
		String sumType = derivType.getSumType();

		out.print("\tstatic public void intensityE( "+derivType.getImageName()+" derivX , "+derivType.getImageName()+" derivY , ImageFloat32 intensity )\n" +
				"\t{\n" +
				"\t\tfinal int w = derivX.width;\n" +
				"\t\tfinal int h = derivY.height;\n" +
				"\n" +
				"\t\tfor( int y = 0; y < h; y++ ) {\n" +
				"\t\t\tint indexX = derivX.startIndex + y*derivX.stride;\n" +
				"\t\t\tint indexY = derivY.startIndex + y*derivY.stride;\n" +
				"\t\t\tint indexI = intensity.startIndex + y*intensity.stride;\n" +
				"\n" +
				"\t\t\tint end = indexX + w;\n" +
				"\t\t\tfor( ; indexX < end; indexX++ , indexY++ , indexI++ ) {\n" +
				"\t\t\t\t"+sumType+" dx = derivX.data[indexX]"+bitWise+";\n" +
				"\t\t\t\t"+sumType+" dy = derivY.data[indexY]"+bitWise+";\n" +
				"\n" +
				"\t\t\t\tintensity.data[indexI] = (float)Math.sqrt(dx*dx + dy*dy);\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void printIntensityAbs(AutoTypeImage derivType) {

		String bitWise = derivType.getBitWise();

		out.print("\tstatic public void intensityAbs( "+derivType.getImageName()+" derivX , "+derivType.getImageName()+" derivY , ImageFloat32 intensity )\n" +
				"\t{\n" +
				"\t\tfinal int w = derivX.width;\n" +
				"\t\tfinal int h = derivY.height;\n" +
				"\n" +
				"\t\tfor( int y = 0; y < h; y++ ) {\n" +
				"\t\t\tint indexX = derivX.startIndex + y*derivX.stride;\n" +
				"\t\t\tint indexY = derivY.startIndex + y*derivY.stride;\n" +
				"\t\t\tint indexI = intensity.startIndex + y*intensity.stride;\n" +
				"\n" +
				"\t\t\tint end = indexX + w;\n" +
				"\t\t\tfor( ; indexX < end; indexX++ , indexY++ , indexI++ ) {\n" +
				"\n" +
				"\t\t\t\tintensity.data[indexI] = Math.abs(derivX.data[indexX]"+bitWise+") +  Math.abs(derivY.data[indexY]"+bitWise+");\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void printDirection(AutoTypeImage derivType) {

		String bitWise = derivType.getBitWise();
		String sumType = derivType.getSumType();

		out.print("\tstatic public void direction( "+derivType.getImageName()+" derivX , "+derivType.getImageName()+" derivY , ImageFloat32 angle )\n" +
				"\t{\n" +
				"\t\tfinal int w = derivX.width;\n" +
				"\t\tfinal int h = derivY.height;\n" +
				"\n" +
				"\t\tfor( int y = 0; y < h; y++ ) {\n" +
				"\t\t\tint indexX = derivX.startIndex + y*derivX.stride;\n" +
				"\t\t\tint indexY = derivY.startIndex + y*derivY.stride;\n" +
				"\t\t\tint indexA = angle.startIndex + y*angle.stride;\n" +
				"\n" +
				"\t\t\tint end = indexX + w;\n" +
				"\t\t\tfor( ; indexX < end; indexX++ , indexY++ , indexA++ ) {\n" +
				"\t\t\t\t"+sumType+" dx = derivX.data[indexX]"+bitWise+";\n" +
				"\t\t\t\t"+sumType+" dy = derivY.data[indexY]"+bitWise+";\n" +
				"\n" +
				"\t\t\t\t// compute the angle while avoiding divided by zero errors\n");
		if( derivType.isInteger() ) {
			out.print("\t\t\t\tangle.data[indexA] = dx == 0 ? (float)(Math.PI/2.0) : (float)Math.atan((double)dy/(double)dx);\n");
		} else {
			out.print("\t\t\t\tangle.data[indexA] = Math.abs(dx) < 1e-10f ? (float)(Math.PI/2.0) : (float)Math.atan(dy/dx);\n");
		}

		out.print("\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateImplGradientToEdgeFeatures app = new GenerateImplGradientToEdgeFeatures();
		app.generate();
	}
}
