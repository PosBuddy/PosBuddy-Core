package de.jkarthaus.posBuddy;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "PosBuddy",
                version = "1.0",
                description = "The best Party PointOfSale System",
                license = @License(name = "Apache 2.0", url = " http://www.apache.org/licenses/"),
                contact = @Contact(url = "https://github.com/PosBuddy", name = "Joern Karthaus")

        )



)

public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
