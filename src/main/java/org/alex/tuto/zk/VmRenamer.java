package org.alex.tuto.zk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Messagebox;

public class VmRenamer {
	private String patternToFind;
	private String replaceWith;

	private List<Media> medias = new ArrayList<>();

	public VmRenamer() {
		patternToFind = "";
		replaceWith = "";
		medias = new ArrayList<>();
	}

	public List<Media> getMedias() { 
		return medias;
	}

	public String getPatternToFind() {
		return patternToFind;
	}

	public void setPatternToFind(String patternToFind) {
		this.patternToFind = patternToFind;
	}

	public String getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(String replaceWith) {
		this.replaceWith = replaceWith;
	}

	@Command("saveUpload")
	@NotifyChange("medias")
	public void upload(@ContextParam(ContextType.TRIGGER_EVENT) UploadEvent event) throws IOException {
		medias.addAll(Arrays.asList(event.getMedias()));
	}

	@Command("removeUpload")
	@NotifyChange("medias")
	public void removeUpload(@BindingParam("media") Media media) {
	   medias.remove(media);
	}

	@Command("renameUpload")
	public void uploadAndRename() throws IOException {
		if ("".equals(patternToFind)) {
			Messagebox.show("There is no pattern to find...", "Error", Messagebox.OK, Messagebox.ERROR);
		} else if (medias.size() > 0) {
			String home = System.getProperty("user.home");
			new File(home + "/Zk_Renamed/").mkdirs();
			String name;
			String renamed;
			for (Media media : medias) {
				byte[] data;
				try {
					data = media.getByteData();
				} catch (IllegalStateException ignored) {
					data = media.getStringData().getBytes();
				}
				name = media.getName();
				renamed = name.replaceFirst(patternToFind, replaceWith);
				Files.write(Paths.get(home + "/Zk_Renamed/" + renamed), data);
				Messagebox.show(name + " copied successfully as " + renamed + " !", "Success", Messagebox.OK, Messagebox.INFORMATION);
			} 
		} else {
			Messagebox.show("No file to rename...", "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}
}
