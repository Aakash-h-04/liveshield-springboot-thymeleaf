package com.liveshield.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QrCodeService {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    /*
     * Generates a QR code containing the supplied value
     * and returns it as a Base64 PNG string.
     */
    public String generateBase64(String value) {

        try {

            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix matrix = writer.encode(
                    value,
                    BarcodeFormat.QR_CODE,
                    WIDTH,
                    HEIGHT
            );

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(
                    matrix,
                    "PNG",
                    output
            );

            return Base64.getEncoder()
                    .encodeToString(
                            output.toByteArray()
                    );

        } catch (WriterException | IOException e) {

            throw new IllegalStateException(
                    "Unable to generate QR code.",
                    e
            );
        }
    }
}