import java.util.Optional;

public class ConfVariable {

    public static String getHost(){
        return Optional.ofNullable(System.getenv("host"))
                .orElse((String)ApplicationProperties.getInstance().get("host"));
    }

    public static String getPath(){
        return Optional.ofNullable(System.getenv("ruta"))
                .orElse((String)ApplicationProperties.getInstance().get("ruta"));
    }

}
