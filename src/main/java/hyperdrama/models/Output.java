package hyperdrama.models;


import java.io.File;

public class Output extends Input {
	public Output(Input input) {
		this.chapters = input.chapters;
		this.meta = input.meta;
		this.texts = input.texts;
		this.fileName = "res";
	}

	public void process() {
		File folder = new File(fileName);
		folder.mkdir();
		chapters.values().forEach(chapter -> {
			chapter.handleTextInsertion(texts);
			chapter.handleLinks(this);
			chapter.writeFileToFolder(folder);
		});
	}

}
