package hyperdrama.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChapterId {
	String key;
	int order;
	String title = "";

	public String getId() {
		return String.format("%s_%d", key, order);
	}

	public boolean valid() {
		return order > 0;
	}

	public String getAsLink() {
		return String.format("<a href=\"./%s.html\">%s</a> ",
				getId(), title.isEmpty() ? "CONTINUE" : title
		);
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ChapterId id && getId().equals(id.getId()) ||
				obj instanceof Chapter chap && getId().equals(chap.getId());
	}
}
