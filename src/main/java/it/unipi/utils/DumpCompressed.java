package it.unipi.utils;

import it.unipi.model.DocumentIndex;
import it.unipi.model.Encoder;
import it.unipi.model.PostingList;
import it.unipi.model.Vocabulary;
import it.unipi.model.implementation.DocumentIndexEntry;
import it.unipi.model.implementation.EliasFano;
import it.unipi.model.implementation.VocabularyEntry;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DumpCompressed implements Dumper{
    // elias fano
    private FileOutputStream fosDocIds;
    private DataOutputStream dosDocIds;

    private boolean opened = false;


    @Override
    public boolean start(String filename) {
        try {
            fosDocIds = new FileOutputStream(filename, true);
            dosDocIds = new DataOutputStream(fosDocIds);
            // TO DO: aggiungere tutti gli altri writers

            opened = true;
        } catch (IOException ie){
            ie.printStackTrace();
            opened = false;
            return false;
        }
        return true;
    }

    @Override
    public void dumpVocabularyEntry(Map.Entry<String, VocabularyEntry> entry) {
        // TO REVIEW: QUALCUNO DOVRà PRENDERSI START OFFSET E ENDOFFSET
        String term = entry.getKey();
        VocabularyEntry vocabularyEntry = entry.getValue();

        PostingList postingList = vocabularyEntry.getPostingList();
        // ELIAS FANO
        Encoder eliasFano = new EliasFano();
        List<Integer> docIdList = postingList.getDocIdList();

        try {
            if (!opened){
                throw new IOException();
            }

            long startOffset = fosDocIds.getChannel().position();

            for (int i = 0; i < docIdList.size(); i += Constants.BLOCK_DIM_ELIASFANO) {
                List<Integer> blockDocIdList = docIdList.subList(i, Math.min(docIdList.size(), i + Constants.BLOCK_DIM_ELIASFANO));
                byte[] byteList = eliasFano.encode(blockDocIdList);

                dosDocIds.write(byteList);
            }

            long endOffset = fosDocIds.getChannel().position();
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }

    public void dumpMergedEntry(Map.Entry<String, VocabularyEntry> entry){
        // TO REVIEW: QUALCUNO DOVRà PRENDERSI START OFFSET E ENDOFFSET
        String term = entry.getKey();
        VocabularyEntry vocabularyEntry = entry.getValue();

        PostingList postingList = vocabularyEntry.getPostingList();
        byte[] docIdCompressedArray = postingList.getCompressedDocIdArray();
        try {
            if (!opened){
                throw new IOException();
            }
            long startOffset = fosDocIds.getChannel().position();
            dosDocIds.write(docIdCompressedArray);
            long endOffset = fosDocIds.getChannel().position();
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }

    @Override
    public void dumpDocumentIndex(DocumentIndex docIndex) {

    }

    @Override
    public void dumpDocumentIndexEntry(Map.Entry<Integer, DocumentIndexEntry> entry) {

    }

    @Override
    public void dumpVocabulary(Vocabulary vocabulary) {
        for (Map.Entry<String, VocabularyEntry> entry : vocabulary.getEntries()) {
            dumpVocabularyEntry(entry);
        }
    }

    @Override
    public boolean end() {
        try {
            if (opened) {
                fosDocIds.close();
                dosDocIds.close();
                opened = false;
                return true;
            }
            else throw new IOException();
        } catch (IOException ie){
            ie.printStackTrace();
            return false;
        }
    }
}
