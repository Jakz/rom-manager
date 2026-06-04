package jack.rm.plugins.fetchers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.github.jakz.romlib.data.assets.AssetKind;

public class AssetFetchSelectionDialog
{
  public static class Entry
  {
    public final AssetKind kind;
    public final URL url;
    boolean available;

    public Entry(AssetKind kind, URL url)
    {
      this.kind = kind;
      this.url = url;
    }

    @Override public String toString()
    {
      return kind.getCaption();
    }
  }

  public static class Result
  {
    public final String name;
    public final int score;
    public final List<Entry> entries;

    public Result(String name, int score, List<Entry> entries)
    {
      this.name = name;
      this.score = score;
      this.entries = entries;
    }

    @Override public String toString()
    {
      return score == 0 ? name : name + " [" + score + "]";
    }
  }

  public static class Selection
  {
    public final Result result;
    public final List<Entry> entries;

    Selection(Result result, List<Entry> entries)
    {
      this.result = result;
      this.entries = entries;
    }
  }

  public static Selection choose(Component parent, String title, List<Result> results, Predicate<Entry> availability)
  {
    if (results.isEmpty())
      return null;

    DefaultListModel<Result> model = new DefaultListModel<>();
    results.forEach(model::addElement);

    JList<Result> list = new JList<>(model);
    list.setSelectedIndex(0);

    JPanel entriesPanel = new JPanel(new GridLayout(0, 1));
    JLabel preview = new JLabel("Select a result", SwingConstants.CENTER);
    preview.setPreferredSize(new Dimension(320, 240));

    SelectionState state = new SelectionState();

    Runnable refreshEntries = () -> loadEntries(list.getSelectedValue(), availability, entriesPanel, preview, state);
    list.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting())
        refreshEntries.run();
    });

    JPanel right = new JPanel(new BorderLayout());
    right.add(entriesPanel, BorderLayout.NORTH);
    right.add(preview, BorderLayout.CENTER);

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), right);
    split.setPreferredSize(new Dimension(820, 380));
    split.setDividerLocation(360);

    refreshEntries.run();

    int result = JOptionPane.showConfirmDialog(parent, split, title, JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE);

    if (result != JOptionPane.OK_OPTION)
      return null;

    Result selected = list.getSelectedValue();
    List<Entry> selectedEntries = state.checkboxes.stream()
        .filter(JCheckBox::isSelected)
        .map(box -> (Entry)box.getClientProperty("entry"))
        .collect(Collectors.toList());

    return selectedEntries.isEmpty() ? null : new Selection(selected, selectedEntries);
  }

  private static void loadEntries(Result result, Predicate<Entry> availability, JPanel entriesPanel, JLabel preview,
      SelectionState state)
  {
    entriesPanel.removeAll();
    state.checkboxes.clear();
    preview.setText("Checking...");
    preview.setIcon(null);

    if (result == null)
      return;

    new SwingWorker<List<Entry>, Void>() {
      @Override protected List<Entry> doInBackground() {
        List<Entry> available = new ArrayList<>();

        for (Entry entry : result.entries)
        {
          entry.available = availability.test(entry);
          if (entry.available)
            available.add(entry);
        }

        return available;
      }

      @Override protected void done() {
        try
        {
          List<Entry> available = get();
          entriesPanel.removeAll();
          state.checkboxes.clear();

          if (available.isEmpty())
          {
            entriesPanel.add(new JLabel("No assets available"));
            preview.setText("No preview");
          }
          else
          {
            for (Entry entry : available)
            {
              JCheckBox box = new JCheckBox(entry.kind.getCaption(), true);
              box.putClientProperty("entry", entry);
              box.addActionListener(e -> loadPreview(entry.url, preview));
              state.checkboxes.add(box);
              entriesPanel.add(box);
            }

            loadPreview(available.get(0).url, preview);
          }

          entriesPanel.revalidate();
          entriesPanel.repaint();
        }
        catch (Exception e)
        {
          preview.setText("Unable to load assets");
        }
      }
    }.execute();
  }

  private static void loadPreview(URL url, JLabel preview)
  {
    preview.setText("Loading...");
    preview.setIcon(null);

    new SwingWorker<ImageIcon, Void>() {
      @Override protected ImageIcon doInBackground() throws Exception {
        Image image = ImageIO.read(url);
        if (image == null)
          return null;

        Image scaled = image.getScaledInstance(320, 240, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
      }

      @Override protected void done() {
        try
        {
          ImageIcon icon = get();
          if (icon != null)
          {
            preview.setText("");
            preview.setIcon(icon);
          }
          else
            preview.setText("No preview");
        }
        catch (Exception e)
        {
          preview.setText("Not found");
        }
      }
    }.execute();
  }

  private static class SelectionState
  {
    final List<JCheckBox> checkboxes = new ArrayList<>();
  }
}
