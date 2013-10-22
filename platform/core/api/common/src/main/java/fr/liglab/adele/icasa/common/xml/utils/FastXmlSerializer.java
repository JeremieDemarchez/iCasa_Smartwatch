/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.common.xml.utils;

import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;


/**
 * This is a quick and dirty implementation of XmlSerializer that isn't horribly
 * painfully slow like the normal one.  It only does what is needed for the
 * specific XML files being written with it.
 * 
 * This code comes from the Android source code.
 */
public class FastXmlSerializer implements XmlSerializer {
    private static final String ESCAPE_TABLE[] = new String[] {
        null,     null,     null,     null,     null,     null,     null,     null,  // 0-7
        null,     null,     null,     null,     null,     null,     null,     null,  // 8-15
        null,     null,     null,     null,     null,     null,     null,     null,  // 16-23
        null,     null,     null,     null,     null,     null,     null,     null,  // 24-31
        null,     null,     "&quot;", null,     null,     null,     "&amp;",  null,  // 32-39
        null,     null,     null,     null,     null,     null,     null,     null,  // 40-47
        null,     null,     null,     null,     null,     null,     null,     null,  // 48-55
        null,     null,     null,     null,     "&lt;",   null,     "&gt;",   null,  // 56-63
    };

    private static final int BUFFER_LEN = 8192;

    private final char[] m_text = new char[BUFFER_LEN];
    private int m_pos;

    private Writer m_writer;

    private OutputStream m_outputStream;
    private CharsetEncoder m_charSet;
    private ByteBuffer m_bytes = ByteBuffer.allocate(BUFFER_LEN);

    private boolean m_inTag;

    private void append(char c) throws IOException {
        int pos = m_pos;
        if (pos >= (BUFFER_LEN-1)) {
            flush();
            pos = m_pos;
        }
        m_text[pos] = c;
        m_pos = pos+1;
    }

    private void append(String str, int i, final int length) throws IOException {
        if (length > BUFFER_LEN) {
            final int end = i + length;
            while (i < end) {
                int next = i + BUFFER_LEN;
                append(str, i, next<end ? BUFFER_LEN : (end-i));
                i = next;
            }
            return;
        }
        int pos = m_pos;
        if ((pos+length) > BUFFER_LEN) {
            flush();
            pos = m_pos;
        }
        str.getChars(i, i+length, m_text, pos);
        m_pos = pos + length;
    }

    private void append(char[] buf, int i, final int length) throws IOException {
        if (length > BUFFER_LEN) {
            final int end = i + length;
            while (i < end) {
                int next = i + BUFFER_LEN;
                append(buf, i, next<end ? BUFFER_LEN : (end-i));
                i = next;
            }
            return;
        }
        int pos = m_pos;
        if ((pos+length) > BUFFER_LEN) {
            flush();
            pos = m_pos;
        }
        System.arraycopy(buf, i, m_text, pos, length);
        m_pos = pos + length;
    }

    private void append(String str) throws IOException {
        append(str, 0, str.length());
    }

    private void escapeAndAppendString(final String string) throws IOException {
        final int N = string.length();
        final char NE = (char)ESCAPE_TABLE.length;
        final String[] escapes = ESCAPE_TABLE;
        int lastPos = 0;
        int pos;
        for (pos=0; pos<N; pos++) {
            char c = string.charAt(pos);
            if (c >= NE) continue;
            String escape = escapes[c];
            if (escape == null) continue;
            if (lastPos < pos) append(string, lastPos, pos-lastPos);
            lastPos = pos + 1;
            append(escape);
        }
        if (lastPos < pos) append(string, lastPos, pos-lastPos);
    }

    private void escapeAndAppendString(char[] buf, int start, int len) throws IOException {
        final char NE = (char)ESCAPE_TABLE.length;
        final String[] escapes = ESCAPE_TABLE;
        int end = start+len;
        int lastPos = start;
        int pos;
        for (pos=start; pos<end; pos++) {
            char c = buf[pos];
            if (c >= NE) continue;
            String escape = escapes[c];
            if (escape == null) continue;
            if (lastPos < pos) append(buf, lastPos, pos-lastPos);
            lastPos = pos + 1;
            append(escape);
        }
        if (lastPos < pos) append(buf, lastPos, pos-lastPos);
    }

    public XmlSerializer attribute(String namespace, String name, String value) throws IOException,
            IllegalArgumentException, IllegalStateException {
        append(' ');
        if (namespace != null) {
            append(namespace);
            append(':');
        }
        append(name);
        append("=\"");

        escapeAndAppendString(value);
        append('"');
        return this;
    }

    public void cdsect(String text) throws IOException, IllegalArgumentException,
            IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void comment(String text) throws IOException, IllegalArgumentException,
            IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void docdecl(String text) throws IOException, IllegalArgumentException,
            IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void endDocument() throws IOException, IllegalArgumentException, IllegalStateException {
        flush();
    }

    public XmlSerializer endTag(String namespace, String name) throws IOException,
            IllegalArgumentException, IllegalStateException {
        if (m_inTag) {
            append(" />\n");
        } else {
            append("</");
            if (namespace != null) {
                append(namespace);
                append(':');
            }
            append(name);
            append(">\n");
        }
        m_inTag = false;
        return this;
    }

    public void entityRef(String text) throws IOException, IllegalArgumentException,
            IllegalStateException {
        throw new UnsupportedOperationException();
    }

    private void flushBytes() throws IOException {
        int position;
        if ((position = m_bytes.position()) > 0) {
            m_bytes.flip();
            m_outputStream.write(m_bytes.array(), 0, position);
            m_bytes.clear();
        }
    }

    public void flush() throws IOException {
        //Log.i("PackageManager", "flush mPos=" + mPos);
        if (m_pos > 0) {
            if (m_outputStream != null) {
                CharBuffer charBuffer = CharBuffer.wrap(m_text, 0, m_pos);
                CoderResult result = m_charSet.encode(charBuffer, m_bytes, true);
                while (true) {
                    if (result.isError()) {
                        throw new IOException(result.toString());
                    } else if (result.isOverflow()) {
                        flushBytes();
                        result = m_charSet.encode(charBuffer, m_bytes, true);
                        continue;
                    }
                    break;
                }
                flushBytes();
                m_outputStream.flush();
            } else {
                m_writer.write(m_text, 0, m_pos);
                m_writer.flush();
            }
            m_pos = 0;
        }
    }

    public int getDepth() {
        throw new UnsupportedOperationException();
    }

    public boolean getFeature(String name) {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public String getNamespace() {
        throw new UnsupportedOperationException();
    }

    public String getPrefix(String namespace, boolean generatePrefix)
            throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    public Object getProperty(String name) {
        throw new UnsupportedOperationException();
    }

    public void ignorableWhitespace(String text) throws IOException, IllegalArgumentException,
            IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void processingInstruction(String text) throws IOException, IllegalArgumentException,
            IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void setFeature(String name, boolean state) throws IllegalArgumentException,
            IllegalStateException {
        if (name.equals("http://xmlpull.org/v1/doc/features.html#indent-output")) {
            return;
        }
        throw new UnsupportedOperationException();
    }

    public void setOutput(OutputStream os, String encoding) throws IOException,
            IllegalArgumentException, IllegalStateException {
        if (os == null)
            throw new IllegalArgumentException();
        if (true) {
            try {
                m_charSet = Charset.forName(encoding).newEncoder();
            } catch (IllegalCharsetNameException e) {
                throw (UnsupportedEncodingException) (new UnsupportedEncodingException(
                        encoding).initCause(e));
            } catch (UnsupportedCharsetException e) {
                throw (UnsupportedEncodingException) (new UnsupportedEncodingException(
                        encoding).initCause(e));
            }
            m_outputStream = os;
        } else {
            setOutput(
                encoding == null
                    ? new OutputStreamWriter(os)
                    : new OutputStreamWriter(os, encoding));
        }
    }

    public void setOutput(Writer writer) throws IOException, IllegalArgumentException,
            IllegalStateException {
        m_writer = writer;
    }

    public void setPrefix(String prefix, String namespace) throws IOException,
            IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException,
            IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void startDocument(String encoding, Boolean standalone) throws IOException,
            IllegalArgumentException, IllegalStateException {
        append("<?xml version='1.0' encoding='utf-8' standalone='"
                + (standalone ? "yes" : "no") + "' ?>\n");
    }

    public XmlSerializer startTag(String namespace, String name) throws IOException,
            IllegalArgumentException, IllegalStateException {
        if (m_inTag) {
            append(">\n");
        }
        append('<');
        if (namespace != null) {
            append(namespace);
            append(':');
        }
        append(name);
        m_inTag = true;
        return this;
    }

    public XmlSerializer text(char[] buf, int start, int len) throws IOException,
            IllegalArgumentException, IllegalStateException {
        if (m_inTag) {
            append(">");
            m_inTag = false;
        }
        escapeAndAppendString(buf, start, len);
        return this;
    }

    public XmlSerializer text(String text) throws IOException, IllegalArgumentException,
            IllegalStateException {
        if (m_inTag) {
            append(">");
            m_inTag = false;
        }
        escapeAndAppendString(text);
        return this;
    }

}
