package testplugin.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class TestView extends ViewPart {

	public TestView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addPropertyListener(IPropertyListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPartControl(Composite parent) {
		Composite comp= new Composite(parent, SWT.NONE);
		comp.setLayout(new FillLayout());
		Text text = new Text(comp, SWT.WRAP);
		text.setText("test");

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public IWorkbenchPartSite getSite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getTitleImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitleToolTip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removePropertyListener(IPropertyListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IViewSite getViewSite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveState(IMemento memento) {
		// TODO Auto-generated method stub

	}

}