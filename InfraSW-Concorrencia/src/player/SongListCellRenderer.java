package player;

import javax.swing.*;
import java.awt.*;

public class SongListCellRenderer extends DefaultListCellRenderer {
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof Song) {
			Song song = (Song) value;
			setText(song.getName());
		}
		return this;
	}
}
