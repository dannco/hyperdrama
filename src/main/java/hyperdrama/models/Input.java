package hyperdrama.models;

import hyperdrama.Runner;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class Input {
	String fileName;
	Meta meta = new Meta();
	NavigableMap<String, Chapter> chapters = new TreeMap<>();
	Map<String, String> texts = new HashMap<>();

	public void setChapters(List<Chapter> chapters) {
		this.chapters.putAll(chapters.stream().collect(Collectors.toMap(
				Chapter::getId, chapter -> chapter
		)));
	}

	public Input merge(Input other) {
		mergeMeta(other);
		mergeTexts(other);
		mergeChapters(other);
		return this;
	}

	private void mergeMeta(Input other) {
		meta.author.addAll(other.meta.author);
		if (!other.meta.description.isEmpty()) {
			if (!meta.description.isEmpty()) {
				Runner.log(String.format(
						"WARNING: story description from overwritten by %s to:%n%s",
						other.fileName, other.meta.description
				));
			} else {
				Runner.log(String.format(
						"INFO: story description set by %s to: %n%s",
						other.fileName, other.meta.description
				));

			}
			meta.description = other.meta.description;
		}
		if (!other.meta.title.isEmpty()) {
			if (!meta.title.isEmpty()) {
				Runner.log(String.format(
						"WARNING: story title from overwritten by %s to:%n%s",
						other.fileName, other.meta.title
				));
			} else {
				Runner.log(String.format(
						"INFO: story title set by %s to: %n%s",
						other.fileName, other.meta.title
				));
			}
			meta.title = other.meta.title;
		}
		if (!other.meta.version.isEmpty()) {
			if (!meta.version.isEmpty()) {
				Runner.log(String.format(
						"WARNING: story version overwritten by %s to:%n%s",
						other.fileName, other.meta.version
				));
			} else {
				Runner.log(String.format(
						"INFO: story version set by %s to: %n%s",
						other.fileName, other.meta.version
				));
			}
			meta.version = other.meta.version;
		}
	}

	public void mergeTexts(Input other) {
		other.texts.forEach((key, value) -> {
			if (texts.containsKey(key)) {
				Runner.log(String.format(
						"WARNING: Text reference %s overwritten by %s",
						key, other.fileName
				));
			} else {
				Runner.log(String.format("INFO: Text reference %s set by %s",
						key, other.fileName
				));

			}
			texts.put(key, value);
		});
	}
	public void mergeChapters(Input other) {
		other.chapters.forEach((key, chapter) -> {
			Optional.ofNullable(chapters.get(key)).ifPresentOrElse(ch -> {
				Runner.log(String.format("WARNING: Chapter %s is set by file %s, but was already declared.",
						ch, other.fileName
				));
				ch.merge(other, chapter);
			}, () -> {
				Runner.log(String.format("INFO: Chapter %s set by file %s.",
						chapter.getId(), other.fileName
				));
				chapters.put(chapter.getId(), chapter);
			});

		});
	}

	public Output asOutput() {
		return new Output(this);
	}
}
