package acast.export.images;

import acast.export.TableExportPage;
import acast.jobs.ICSVExportConstants;
import acast.ui.sds.SafetyConstraintView;
import acast.wizards.AbstractExportWizard;
import messages.Messages;

public class SafetyConstraintsIMGWizard extends AbstractExportWizard{
	public SafetyConstraintsIMGWizard() {
		super(SafetyConstraintView.ID);
		String[] filters = new String[] {"*.png"}; //$NON-NLS-1$ //$NON-NLS-2$ 
		this.setExportPage(new TableExportPage(filters,
				ICSVExportConstants.SAFETY_CONSTRAINT + Messages.AsImage));
	}

	@Override
	public boolean performFinish() {
		return this.performXSLExport(				
				"/fopSafetyConstraints.xsl", "Exporting Image", false); ////$NON-NLS-1$
	}
}