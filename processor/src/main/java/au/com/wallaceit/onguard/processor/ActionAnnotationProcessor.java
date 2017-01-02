package au.com.wallaceit.onguard.processor;

/*
 * Copyright 2016 Michael Boyde Wallace (http://wallaceit.com.au)
 * This file is part of OnGuard.
 *
 * OnGuard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OnGuard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OnGuard (COPYING). If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by michael on 31/12/16.
 */

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("au.com.wallaceit.onguard.processor.GeofenceAction")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ActionAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        StringBuilder builder = new StringBuilder()
                .append("package au.com.wallaceit.onguard.actions;\n\n")
                .append("public class ActionsList {\n\n") // open class
                .append("\tpublic static Class[] CLASSES = new Class[]{\n"); // open method
                //.append("\t\treturn \"");


        // for each javax.lang.model.element.Element annotated with the CustomAnnotation
        for (Element element : roundEnv.getElementsAnnotatedWith(GeofenceAction.class)) {
            if (element.getSimpleName().toString().equals("GeofenceActionPlugin"))
                continue;

            String objectType = element.getSimpleName().toString();
            // this is appending to the return statement
            builder.append("\t\t").append(objectType).append(".class,\n");
        }


        builder.append("\t};\n") // close array
                .append("}\n"); // close class



        try { // write the file
            JavaFileObject source = processingEnv.getFiler().createSourceFile("au.com.wallaceit.onguard.actions.ActionsList");

            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }

        return true;
    }
}
