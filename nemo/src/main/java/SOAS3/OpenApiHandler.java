package SOAS3;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class OpenApiHandler {
    /*
     * This class handles openapi files.
     */
    private OpenAPI openapi;

    public OpenApiHandler() {
        // TODO Auto-generated constructor stub
    }

    public OpenAPI ReadFile(String address)
    {
        this.openapi= new OpenAPIV3Parser().read(address);
        return openapi;
    }

    public OpenAPI getOpenapi() {
        return openapi;
    }

    public void setOpenapi(OpenAPI openapi) {
        this.openapi = openapi;

    }
}
