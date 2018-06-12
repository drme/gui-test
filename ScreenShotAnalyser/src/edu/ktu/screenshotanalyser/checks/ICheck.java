package edu.ktu.screenshotanalyser.checks;


public interface ICheck {
	CheckResult[] analyze(CheckRequest request, CheckContext context);
}
