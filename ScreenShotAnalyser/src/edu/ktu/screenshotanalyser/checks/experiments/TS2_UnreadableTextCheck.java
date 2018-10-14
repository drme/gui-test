package edu.ktu.screenshotanalyser.checks.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ktu.screenshotanalyser.checks.CheckRequest;
import edu.ktu.screenshotanalyser.checks.CheckResult;
import edu.ktu.screenshotanalyser.checks.ICheck;
import edu.ktu.screenshotanalyser.context.AppContext;
import edu.ktu.screenshotanalyser.texts.TextExtractor.ExtractedText;

public class TS2_UnreadableTextCheck implements ICheck {

	String type = "TS2";
	Map<String, DeviceSize> devices = new HashMap<>();
	double hMin = 2;
	double wMin = 2;

	public TS2_UnreadableTextCheck() {
		this.devices.put("Nexus_One", new DeviceSize(59.8, 119));
	}

	@Override
	public CheckResult[] analyze(CheckRequest request, AppContext context) {
		List<CheckResult> results = new ArrayList<>();
		System.out.println(request.getDevice());
		if (!this.devices.containsKey(request.getDevice())) {
			System.out.println("Can't find device: " + request.getDevice());
			return new CheckResult[0];
		}
		DeviceSize dev = this.devices.get(request.getDevice());

		double h = request.getH();
		double w = request.getW();

		double hRatio = dev.heightInMM / h;
		double wRatio = dev.widthInMM / w;
		for (ExtractedText text : request.getExtractedTexts()) {
			if (text.getText().trim().isEmpty()) {
				continue;
			}
			double actualH = text.getArea().height * hRatio;
			double actualW = text.getArea().width * wRatio;
			if (actualH < this.hMin) {
				results.add(CheckResult.Nok(type,
						String.format("Found text with physical height less than %s mm, actual %s mm for text: %s",

								this.hMin, actualH, text.getText()),

						request.getOriginalFile(), "-"));

			}

		}
		return results.toArray(new CheckResult[0]);
	}

	private static class DeviceSize {
		double widthInMM;
		double heightInMM;

		public DeviceSize(double w, double h) {
			this.widthInMM = w;
			this.heightInMM = h;
		}
	}

}
