package com.github.aleksikangas.qct.ui.file;

import com.github.aleksikangas.qct.core.QctFile;
import net.miginfocom.swing.MigLayout;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class FilePanel extends JPanel implements QctFileAware {
  private final JLabel fileLabel = new JLabel("File:");
  private final JTextField fileTextField = new JTextField();
  private final JButton fileBrowseButton = new JButton("Browse...");
  private final JButton decodeButton = new JButton("Decode");

  private final QctFileService qctFileService;

  public FilePanel(final QctFileService qctFileService) {
    super(new MigLayout("fill", "[][][fill, grow]", "[][]"));
    this.qctFileService = Objects.requireNonNull(qctFileService);

    fileTextField.setEnabled(false);
    fileBrowseButton.addActionListener(e -> selectFile());
    decodeButton.addActionListener(e -> decodeSelectedFile());

    setDecodeButtonEnabled(false);

    add(fileLabel);
    add(fileTextField, "skip, wrap");
    add(fileBrowseButton, "");
    add(decodeButton, "wrap");
  }

  @Override
  public void addNotify() {
    super.addNotify();
    qctFileService.bind(this);
  }

  @Override
  public void removeNotify() {
    super.removeNotify();
    qctFileService.unbind(this);
  }

  @Override
  public void onQctFile(@Nullable final QctFile qctFile) {
    fileBrowseButton.setEnabled(true);
    setDecodeButtonEnabled(true);

    revalidate();
    repaint();
  }

  private void selectFile() {
    final var fileChooser = new JFileChooser();
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setFileFilter(new FileNameExtensionFilter("QCT file", "qct"));
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      final File selectedFile = fileChooser.getSelectedFile();
      fileTextField.setText(selectedFile.getPath());
    }
    setDecodeButtonEnabled(true);
  }

  private void decodeSelectedFile() {
    fileBrowseButton.setEnabled(false);
    setDecodeButtonEnabled(false);
    qctFileService.decodeQctFile(Path.of(fileTextField.getText()));
  }

  private void setDecodeButtonEnabled(final boolean enabled) {
    decodeButton.setEnabled(!fileTextField.getText().isBlank() &&
                                Files.exists(Path.of(fileTextField.getText()))
                                && enabled);
  }
}
