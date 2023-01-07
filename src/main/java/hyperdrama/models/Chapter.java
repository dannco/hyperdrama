package hyperdrama.models;

import hyperdrama.Runner;
import hyperdrama.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Setter
public class Chapter extends ChapterId {
	String text;

	String title = "";
	List<ChapterId> back = new ArrayList<>();
	List<ChapterId> forward = new ArrayList<>();

	public void setText(List<String> texts) {
		text = String.join("", texts);
	}

	public void merge(Input input, Chapter chapter) {
		if (!chapter.text.isEmpty()) {
			if (text.isEmpty()) {
				Runner.log(String.format("INFO: %s empty text set by %s",
						chapter.getId(), input.fileName
				));
			} else {
				Runner.log(String.format("WARNING: %s text overwritten by %s",
						chapter.getId(), input.fileName
				));
			}
			text = chapter.text;
		}
		if (!chapter.title.isEmpty()) {
			if (title.isEmpty()) {
				Runner.log(String.format("INFO: %s empty title set by %s",
						chapter.getId(), input.fileName
				));
			} else {
				Runner.log(String.format("WARNING: %s title overwritten by %s",
						chapter.getId(), input.fileName
				));
			}
			title = chapter.title;
		}
		back.addAll(chapter.back);
		forward.addAll(chapter.forward);
	}


	private static final Pattern textInsertPattern =
			Pattern.compile("\\$([a-zA-Z_0-9]+)");
	public void handleTextInsertion(Map<String, String> textRepo) {
		Matcher matcher = textInsertPattern.matcher(text);
		while (matcher.find()) {
			String key = matcher.group(1);
			if (textRepo.containsKey(key)) {
				text = text.replace(matcher.group(), textRepo.get(key));
			} else {
				Runner.log(String.format(
						"WARNING: %s references text insertion key %s, which is not in texts",
						getId(), key
				));
			}
		}
	}

	private static final Pattern chapterLinkPattern =
			Pattern.compile(
					"@(?<key>[a-zA-Z_]+)" +
							"\\((?<text>[^\\)]+)\\)" +
							"(:(?<cron>\\d+))?");

	public void handleLinks(Input context) {
		back.forEach(backlink ->
				Optional.ofNullable(context.chapters.floorEntry(backlink.getId())).ifPresentOrElse(
						(entry) -> backlink.order = entry.getValue().order,
						() -> backlink.order = -1
				));
		forward.forEach(forwardLink ->
				Optional.ofNullable(context.chapters.ceilingEntry(forwardLink.getId())).ifPresentOrElse(
						(entry) -> forwardLink.order = entry.getValue().order,
						() -> forwardLink.order = -1
				));
		back.removeIf(chapter -> !chapter.valid());
		forward.removeIf(chapter -> !chapter.valid());
		Optional.ofNullable(context.chapters.lowerEntry(getId())).ifPresent((entry) -> {
			if (!entry.getValue().key.equals(key))
				return;
			if (!back.contains(entry.getValue())) {
				back.add(entry.getValue());
			}
		});
		Optional.ofNullable(context.chapters.higherEntry(getId())).ifPresent((entry) -> {
			if (!entry.getValue().key.equals(key))
				return;
			if (!forward.contains(entry.getValue())) {
				forward.add(entry.getValue());
			}
		});
		Matcher matcher = chapterLinkPattern.matcher(text);
		while (matcher.find()) {
			ChapterId id = new ChapterId();
			id.title = matcher.group("text");
			id.setKey(matcher.group("key"));
			id.setOrder(Optional.ofNullable(matcher.group("cron"))
					.map(Integer::parseInt).orElse(order));
			Optional.ofNullable(context.chapters.floorEntry(id.getId())).ifPresentOrElse(
					(entry) -> id.order = entry.getValue().order,
					() -> id.order = -1
			);
			if (!id.valid())
				continue;
			text = text.replace(matcher.group(), id.getAsLink(true));
		}
	}

	public String toHtml() {
		return String.format("<div class=\"back-links\">%s</div>%n" + (
				title.isEmpty() ? "" : String.format("<div class=\"chapter-title\">%s</div>", title)
				) + "<div class=\"chapter-text\">%s</div><div class=\"forward-links\">%s</div>",
				back.stream().map(ch -> ch.getAsLink(false)).collect(Collectors.joining(" || ")),
				text.replaceAll("\n", "<br>"),
				forward.stream().map(ch -> ch.getAsLink(true)).collect(Collectors.joining(" || "))
		);
	}

	public void writeFileToFolder(File folder) {
		FileUtils.writeToFile(folder.getAbsolutePath() + "/" + getId() + ".html", toHtml());
	}
}
