package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.ui.file.QctFileAware;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import net.miginfocom.swing.MigLayout;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Objects;

public final class MetadataPanel extends JPanel implements QctFileAware {
  private final QctFileService qctFileService;

  private final JLabel magicNumberLabel = new JLabel("Magic Number:");
  private final JTextField magicNumberField = new JTextField();

  private final JLabel fileFormatVersionLabel = new JLabel("File Format Version:");
  private final JTextField fileFormatVersionField = new JTextField();

  private final JLabel widthLabel = new JLabel("Width (tiles / pixels):");
  private final JTextField widthField = new JTextField();

  private final JLabel heightLabel = new JLabel("Height (tiles / pixels):");
  private final JTextField heightField = new JTextField();

  private final JLabel longTitleLabel = new JLabel("Long Title:");
  private final JTextField longTitleField = new JTextField();

  private final JLabel nameLabel = new JLabel("Name:");
  private final JTextField nameField = new JTextField();

  private final JLabel identifierLabel = new JLabel("Identifier:");
  private final JTextField identifierField = new JTextField();

  private final JLabel editionLabel = new JLabel("Edition:");
  private final JTextField editionField = new JTextField();

  private final JLabel revisionLabel = new JLabel("Revision:");
  private final JTextField revisionField = new JTextField();

  private final JLabel keywordsLabel = new JLabel("Keywords:");
  private final JTextField keywordsField = new JTextField();

  private final JLabel copyrightLabel = new JLabel("Copyright:");
  private final JTextField copyrightField = new JTextField();

  private final JLabel scaleLabel = new JLabel("Scale:");
  private final JTextField scaleField = new JTextField();

  private final JLabel datumLabel = new JLabel("Datum:");
  private final JTextField datumField = new JTextField();

  private final JLabel depthsLabel = new JLabel("Depths:");
  private final JTextField depthsField = new JTextField();

  private final JLabel heightsLabel = new JLabel("Heights:");
  private final JTextField heightsField = new JTextField();

  private final JLabel projectionLabel = new JLabel("Projection:");
  private final JTextField projectionField = new JTextField();

  private final JLabel flagsLabel = new JLabel("Flags:");
  private final JTextField flagsField = new JTextField();

  private final JLabel originalFileNameLabel = new JLabel("Original File Name:");
  private final JTextField originalFileNameField = new JTextField();

  private final JLabel originalFileSizeLabel = new JLabel("Original File Size:");
  private final JTextField originalFileSizeField = new JTextField();

  private final JLabel originalFileCreationTimeLabel = new JLabel("Original File Creation Time:");
  private final JTextField originalFileCreationTimeField = new JTextField();

  // --- Extended Data ---
  private final JLabel mapTypeLabel = new JLabel("Map Type:");
  private final JTextField mapTypeField = new JTextField();

  private final JLabel datumShiftLabel = new JLabel("Datum Shift:");
  private final JPanel datumShiftPanel;

  private final JLabel diskNameLabel = new JLabel("Disk Name:");
  private final JTextField diskNameField = new JTextField();

  private final JLabel licenseInformationLabel = new JLabel("License Information");
  private final JPanel licenseInformationPanel;

  private final JLabel associatedDataLabel = new JLabel("Associated Data:");
  private final JTextField associatedDataField = new JTextField();

  private final JLabel digitalMapShopLabel = new JLabel("Digital Map Shop:");
  private final JPanel digitalMapShopPanel;
  // --- Extended Data ---

  private final JLabel mapOutlineLabel = new JLabel("Map Outline:");
  private final JPanel mapOutlinePanel;

  public MetadataPanel(final QctFileService qctFileService) {
    super(new MigLayout("fill, insets 4 10 4 10, gap 10", "[][fill, grow]", ""));
    this.qctFileService = Objects.requireNonNull(qctFileService);
    setBorder(BorderFactory.createTitledBorder("Metadata"));

    datumShiftPanel = new DatumShiftPanel(qctFileService);
    licenseInformationPanel = new LicenseInformationPanel(qctFileService);
    digitalMapShopPanel = new DigitalMapShopPanel(qctFileService);
    mapOutlinePanel = new MapOutlinePanel(qctFileService);

    setFieldsEnabled(false);

    add(magicNumberLabel);
    add(magicNumberField, "wrap");

    add(fileFormatVersionLabel);
    add(fileFormatVersionField, "wrap");

    add(widthLabel);
    add(widthField, "wrap");

    add(heightLabel);
    add(heightField, "wrap");

    add(longTitleLabel);
    add(longTitleField, "wrap");

    add(nameLabel);
    add(nameField, "wrap");

    add(identifierLabel);
    add(identifierField, "wrap");

    add(editionLabel);
    add(editionField, "wrap");

    add(revisionLabel);
    add(revisionField, "wrap");

    add(keywordsLabel);
    add(keywordsField, "wrap");

    add(copyrightLabel);
    add(copyrightField, "wrap");

    add(scaleLabel);
    add(scaleField, "wrap");

    add(datumLabel);
    add(datumField, "wrap");

    add(depthsLabel);
    add(depthsField, "wrap");

    add(heightsLabel);
    add(heightsField, "wrap");

    add(projectionLabel);
    add(projectionField, "wrap");

    add(flagsLabel);
    add(flagsField, "wrap");

    add(originalFileNameLabel);
    add(originalFileNameField, "wrap");

    add(originalFileSizeLabel);
    add(originalFileSizeField, "wrap");

    add(originalFileCreationTimeLabel);
    add(originalFileCreationTimeField, "wrap");

    add(mapTypeLabel);
    add(mapTypeField, "wrap");

    add(datumShiftLabel);
    add(datumShiftPanel, "wrap");

    add(diskNameLabel);
    add(diskNameField, "wrap");

    add(licenseInformationLabel);
    add(licenseInformationPanel, "wrap");

    add(associatedDataLabel);
    add(associatedDataField, "wrap");

    add(digitalMapShopLabel);
    add(digitalMapShopPanel, "wrap");

    add(mapOutlineLabel);
    add(mapOutlinePanel, "wrap");
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
    if (qctFile != null) {
      display(qctFile.metadata());
    } else {
      clear();
    }
    revalidate();
    repaint();
  }

  private void display(final Metadata metadata) {
    magicNumberField.setText(metadata.magicNumber().toString());
    fileFormatVersionField.setText(metadata.fileFormatVersion().toString());
    widthField.setText(String.format("%d / %d", metadata.widthTiles(), metadata.widthPixels()));
    heightField.setText(String.format("%d / %d", metadata.heightTiles(), metadata.heightPixels()));
    longTitleField.setText(metadata.longTitle());
    nameField.setText(metadata.name());
    identifierField.setText(metadata.identifier());
    editionField.setText(metadata.edition());
    revisionField.setText(metadata.revision());
    keywordsField.setText(metadata.keywords());
    copyrightField.setText(metadata.copyright());
    scaleField.setText(metadata.scale());
    datumField.setText(metadata.datum());
    depthsField.setText(metadata.depths());
    heightsField.setText(metadata.heights());
    projectionField.setText(metadata.projection());
    flagsField.setText(metadata.flags().toString());
    originalFileNameField.setText(metadata.originalFileName());
    originalFileSizeField.setText(String.valueOf(metadata.originalFileSize()));
    originalFileSizeField.setToolTipText(String.format("KB: %d\nMB: %d\nGB: %.2f",
                                                       metadata.originalFileSize() / 1000,
                                                       metadata.originalFileSize() / 1000 / 1000,
                                                       metadata.originalFileSize() / 1000.0 / 1000.0 / 1000.0));
    originalFileCreationTimeField.setText(metadata.originalFileCreationTime().toString());
    mapTypeField.setText(metadata.extendedData().mapType());
    diskNameField.setText(metadata.extendedData().diskName());
    associatedDataField.setText(metadata.extendedData().associatedData());
  }

  private void setFieldsEnabled(final boolean enabled) {
    magicNumberField.setEnabled(enabled);
    fileFormatVersionField.setEnabled(enabled);
    widthField.setEnabled(enabled);
    heightField.setEnabled(enabled);
    longTitleField.setEnabled(enabled);
    nameField.setEnabled(enabled);
    identifierField.setEnabled(enabled);
    editionField.setEnabled(enabled);
    revisionField.setEnabled(enabled);
    keywordsField.setEnabled(enabled);
    copyrightField.setEnabled(enabled);
    scaleField.setEnabled(enabled);
    datumField.setEnabled(enabled);
    depthsField.setEnabled(enabled);
    heightsField.setEnabled(enabled);
    projectionField.setEnabled(enabled);
    flagsField.setEnabled(enabled);
    originalFileNameField.setEnabled(enabled);
    originalFileSizeField.setEnabled(enabled);
    originalFileCreationTimeField.setEnabled(enabled);
    mapTypeField.setEnabled(enabled);
    diskNameField.setEnabled(enabled);
    associatedDataField.setEnabled(enabled);
  }

  private void clear() {
    magicNumberField.setText("");
    fileFormatVersionField.setText("");
    widthField.setText("");
    heightsField.setText("");
    longTitleField.setText("");
    nameField.setText("");
    identifierField.setText("");
    editionField.setText("");
    revisionField.setText("");
    keywordsField.setText("");
    copyrightField.setText("");
    scaleField.setText("");
    datumField.setText("");
    depthsField.setText("");
    heightsField.setText("");
    projectionField.setText("");
    flagsField.setText("");
    originalFileNameField.setText("");
    originalFileSizeField.setText("");
    originalFileCreationTimeField.setText("");
    mapTypeField.setText("");
    diskNameField.setText("");
    associatedDataField.setText("");
  }
}
