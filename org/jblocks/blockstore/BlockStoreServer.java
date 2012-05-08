package org.jblocks.blockstore;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jblocks.JBlocks;
import org.jblocks.editor.BlockModel;
import org.jblocks.scriptengine.CodeIO;
import org.jblocks.utils.StreamUtils;
import org.xml.sax.SAXException;

/**
 *
 * This class provides methods to download, search and upload to the JBlocks blocks database. <br />
 * But please, don't spam! <br />
 * <br />
 * The server is currenlty my (ZeroLuck's) server <i>zero-bgn.de</i>. <br />
 * 
 * @author ZeroLuck
 */
public class BlockStoreServer {

    private static final String ADD_BLOCK = "http://zero-bgn.de/js/jblocks/addblock.php";
    private static final String GET_BLOCK = "http://zero-bgn.de/js/jblocks/getblock.php";
    private static final String SEARCH_BLOCK = "http://zero-bgn.de/js/jblocks/searchblock.php";
    private static final String NEWEST_BLOCKS = "http://zero-bgn.de/js/jblocks/newestblock.php";

    /**
     * Uploads data with a specified ID to the server. <br />
     * Use {@link #download(long) } to download the data. <br />
     * The maximum file size is 2<sup>16</sup> bytes and the maximum spec length is 255 bytes.
     * <p />
     * The server may change in future.
     * 
     * @param id the ID of the data
     * @param blockData the data to upload
     * @throws IOException if an error occurs
     */
    static void upload(long id, String spec, byte[] blockData) throws IOException {
        if (spec == null || id == BlockModel.NOT_AN_ID || spec.getBytes().length > 255) {
            throw new IllegalArgumentException();
        }

        OutputStream out = null;
        InputStream in = null;
        byte[] data = null;
        try {
            URLConnection conn = new URL(ADD_BLOCK + "?id=" + id).openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            out = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write("spec=" + URLEncoder.encode(spec, "UTF-8") + "&data=" + URLEncoder.encode(new String(blockData), "UTF-8"));
            writer.flush();
            conn.connect();

            in = conn.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[0xFF];
            int len;
            while ((len = in.read(buf)) != -1) {
                bout.write(buf, 0, len);
            }
            data = bout.toByteArray();
            String str = new String(data);
            if (str.startsWith("Error code")) {
                throw new IOException("block store returned: " + str);
            }
            if (str.startsWith("false")) {
                throw new IOException("couldn't upload block!");
            }
        } finally {
            StreamUtils.safeClose(in);
            StreamUtils.safeClose(out);
        }
    }

    /**
     * Downloads the data for the specified ID from the server. <br />
     * Use {@link #upload(long, byte[]) } to upload. <br />
     * <p />
     * The server may change in future. 
     * 
     * @param id the ID of the data to download
     * @return the data
     * @throws IOException if an error occurs
     */
    static byte[] download(long id) throws IOException {
        if (id == BlockModel.NOT_AN_ID) {
            throw new IllegalArgumentException();
        }

        InputStream in = null;
        byte[] data = null;
        try {
            URLConnection conn = new URL(GET_BLOCK + "?id=" + id).openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.connect();

            in = conn.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[0xFF];
            int len;
            while ((len = in.read(buf)) != -1) {
                bout.write(buf, 0, len);
            }
            data = bout.toByteArray();
        } finally {
            StreamUtils.safeClose(in);
        }
        return data;
    }

    /**
     * Searches for a specified keyword in the database. <br />
     * A <code>%</code> in the keyword stands for a regexp-unknown string. <br />
     * Note: The spec is encoded like this: <i>SYNTAX</i>;<i>CATEGORY</i>;<i>TYPE</i><br />
     * 
     * @param keyword the keyword which to search for
     * @return the search result
     * @throws IOException 
     */
    static SearchResult[] search(String keyword) throws IOException {
        if (keyword == null) {
            throw new IllegalArgumentException();
        }

        InputStream in = null;
        byte[] data = null;
        try {
            URLConnection conn = new URL(SEARCH_BLOCK + "?keyword=" + URLEncoder.encode(keyword, "UTF-8")).openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.connect();

            in = conn.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[0xFF];
            int len;
            while ((len = in.read(buf)) != -1) {
                bout.write(buf, 0, len);
            }
            data = bout.toByteArray();
        } finally {
            StreamUtils.safeClose(in);
        }
        String res = new String(data);

        String[] split = res.split("\n");
        SearchResult[] result = new SearchResult[split.length / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = new SearchResult(Long.valueOf(split[i * 2]), split[i * 2 + 1]);
        }
        return result;
    }

    /**
     * Returns the newest blocks. <br />
     * (Maximum: <= 30)
     * 
     * @return The newest blocks in the block store
     * @throws IOException 
     */
    static SearchResult[] newest() throws IOException {
        InputStream in = null;
        byte[] data = null;
        try {
            URLConnection conn = new URL(NEWEST_BLOCKS).openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.connect();

            in = conn.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[0xFF];
            int len;
            while ((len = in.read(buf)) != -1) {
                bout.write(buf, 0, len);
            }
            data = bout.toByteArray();
        } finally {
            StreamUtils.safeClose(in);
        }
        String res = new String(data);

        String[] split = res.split("\n");
        SearchResult[] result = new SearchResult[split.length / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = new SearchResult(Long.valueOf(split[i * 2]), split[i * 2 + 1]);
        }
        return result;
    }

    /**
     * Creates a {@code String} which contains all specified {@code String}s. <br />
     * Use {@link #decode(java.lang.String)} to decode the {@code String} again. <br />
     * 
     * @param text the array of Strings
     * @return a created String which contains all the specfied Strings
     */
    static String encode(String... text) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : text) {
            if (!first) {
                sb.append(";");
            }
            first = false;
            sb.append(s.replaceAll(";", ";;"));
        }
        return sb.toString();
    }

    /**
     * Creates a {@code String[]} from a specified encoded {@code String}. <br />
     * The {@code String} has to be encoded with {@link #encode(java.lang.String[]) }. <br />
     * 
     * @param text the encoded String
     * @return the decoded Strings
     */
    static String[] decode(String text) {
        List<String> splits = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            char n = i + 1 < len ? text.charAt(i + 1) : 0;
            if (c == ';') {
                if (n == ';') {
                    sb.append(';');
                    i++;
                } else {
                    splits.add(sb.toString());
                    sb = new StringBuilder();
                }
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            splits.add(sb.toString());
        }
        return splits.toArray(new String[]{});
    }

    public static void uploadBlock(JBlocks ctx, BlockModel model) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        CodeIO.writeBlock(ctx, data, model);
        upload(model.getID(), encode(model.getSyntax(), model.getCategory(), model.getType()), data.toByteArray());
    }

    static BlockModel downloadBlock(JBlocks ctx, long id) throws IOException {
        byte[] data = download(id);
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        try {
            return CodeIO.readBlock(ctx, in);
        } catch (SAXException sax) {
            throw new IOException("Can't read the block!", sax);
        } catch (ParserConfigurationException ex) {
            throw new IOException("Can't read the block!", ex);
        }
    }

    private static BlockModel[] createModels(SearchResult[] res) {
        BlockModel[] models = new BlockModel[res.length];
        int off = 0;
        for (SearchResult r : res) {
            String[] enq = decode(r.blockspec);
            if (enq.length != 3) {
                models[off++] = BlockModel.createModel("command", "", "Obsolete", BlockModel.NOT_AN_ID);
            } else {
                models[off++] = BlockModel.createModel(enq[2], enq[1], enq[0], r.id);
            }
        }

        return models;
    }

    static BlockModel[] searchBlocks(String keywords, String categoryFilter, String typeFilter) throws IOException {
        String[] split = keywords.replace("%", "").split(" ");
        String search = "%";
        for (String s : split) {
            if (!s.trim().isEmpty()) {
                search += s + "%";
            }
        }
        final String finalSearch = encode(search,
                (categoryFilter == null) ? "%" : categoryFilter,
                (typeFilter == null) ? "%" : typeFilter);
        
        return createModels(search(finalSearch));
    }

    static BlockModel[] newestBlocks() throws IOException {
        return createModels(newest());
    }

    static class SearchResult {

        final long id;
        final String blockspec;

        public SearchResult(long id, String spec) {
            this.id = id;
            this.blockspec = spec;
        }

        @Override
        public String toString() {
            return "SearchResult[id=" + id + ", spec=" + blockspec + "]";
        }
    }
}
