package hyperdrama.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Meta {

	String title = "";
	String version = "";
	String description = "";
	List<String> author = new ArrayList<>();

}
