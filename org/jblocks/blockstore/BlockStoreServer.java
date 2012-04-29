package org.jblocks.blockstore;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import org.jblocks.editor.BlockModel;
import org.jblocks.utils.StreamUtils;

/**
 *
 * This class provides methods to download, search and upload to the JBlocks blocks database. <br />
 * But please, don't spam! <br />
 * <br />
 * The server is currenlty my (ZeroLuck's) server <i>zero-bgn.de</i>. <br />
 * 
 * @author ZeroLuck
 */
class BlockStoreServer {

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
    
    public static void main(String[] args) throws IOException {
        upload(2002, "hallo %{s} welt", "data".getBytes());
        System.out.println(Arrays.toString(search("test")));
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
