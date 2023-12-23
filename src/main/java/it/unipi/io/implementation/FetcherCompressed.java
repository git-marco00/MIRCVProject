package it.unipi.io.implementation;

import it.unipi.model.DocumentIndexEntry;
import it.unipi.model.PostingList;
import it.unipi.model.VocabularyEntry;
import it.unipi.model.implementation.PostingListCompressedImpl;
import it.unipi.encoding.CompressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class FetcherCompressed extends FetcherBinary {

    private static final Logger logger = LoggerFactory.getLogger(FetcherCompressed.class);

    public FetcherCompressed() {
        compression = CompressionType.COMPRESSED;
    }

    private byte[] fetchBytes(FileInputStream stream, long startOffset, int length) throws IOException {
        if (!opened)
            throw new IOException("Fetcher has not been started");

        byte[] bytes = new byte[length];
        stream.getChannel().position(startOffset);

        if (stream.read(bytes) != length)
            throw new IOException("Could not fetch posting list");

        return bytes;
    }

    public byte[] fetchCompressedDocIds(long startOffset, int length) throws IOException {
        return fetchBytes(docIdsReader, startOffset, length);
    }

    public byte[] fetchCompressedTermFrequencies(long startOffset, int length) throws IOException {
        return fetchBytes(termFreqReader, startOffset, length);
    }

    @Override
    protected void loadPosting(VocabularyEntry entry) throws IOException {
        PostingList postingList = entry.getPostingList();

        if (!(postingList instanceof PostingListCompressedImpl pList)) {
            logger.error("Cannot fetch uncompressed list");
            throw new UnsupportedOperationException("Load of uncompressed list");
        }

        pList.setCompressedDocIds(
                fetchCompressedDocIds(entry.getDocIdsOffset(), entry.getDocIdsLength())
        );
        pList.setCompressedTermFrequencies(
                fetchCompressedTermFrequencies(entry.getTermFreqOffset(), entry.getTermFreqLength())
        );

        pList.loadNextBlock();
    }

    @Override
    public Map.Entry<String, VocabularyEntry> loadVocEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, DocumentIndexEntry> loadDocEntry() {
        throw new UnsupportedOperationException();
    }
}