package de.mazdermind.gintercom.matrix.webui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebuiController {

	@GetMapping("/")
	public String redirectToEntryPoint() {
		return "redirect:/ui/";
	}

	@GetMapping("/ui/*")
	public String renderAngularTemplate() {
		return "angular";
	}
}
