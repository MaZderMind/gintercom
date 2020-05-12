package de.mazdermind.gintercom.matrix.webui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebuiController {
	@Value("${git.commit.id.describe}")
	private String version;

	@GetMapping("/")
	public String redirectToEntryPoint() {
		return "redirect:/ui/";
	}

	@GetMapping("/ui/*")
	public String renderAngularTemplate(ModelMap modelMap) {
		modelMap.addAttribute("version", version);
		return "angular";
	}
}
