package Presentacion;

import java.awt.Component;
import java.awt.Window;
import javax.swing.SwingUtilities;

public final class DashboardWindowSupport {

    private DashboardWindowSupport() {
    }

    public static DashboardJFrame findDashboardFrame(Component component) {
        Window window = component == null ? null : SwingUtilities.getWindowAncestor(component);

        while (window != null) {
            if (window instanceof DashboardJFrame frame) {
                return frame;
            }

            window = window.getOwner();
        }

        return null;
    }
}
