package Assignment2.Task3AndTask4;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class CompareAlgorithms {

    IndexReader indexReader;
    IndexSearcher indexSearcher;
    Analyzer analyzer;
    String fileName;
    FileWriter fileWriter;

    public CompareAlgorithms(String indexPath, Similarity similarity, String fileName) {
        try {
            this.indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            this.indexSearcher = new IndexSearcher(indexReader);
            this.analyzer = new StandardAnalyzer();
            this.indexSearcher.setSimilarity(similarity);
            this.fileName = fileName;
            this.fileWriter = new FileWriter(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculateForGivenAlgorithms(String queryString, int count) {
        QueryParser parser = new QueryParser("TEXT", analyzer);
        try {
            Query query = parser.parse(QueryParser.escape(queryString));
            TopDocs results = indexSearcher.search(query, 1000);

            ScoreDoc[] hits = results.scoreDocs;
            printToFile(hits, count);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

    }

    private void printToFile(ScoreDoc[] hits, int count) throws IOException {
        StringBuilder strToPrint = new StringBuilder();
        for (int i = 0; i < hits.length; i++) {
            Document doc = indexSearcher.doc(hits[i].doc);
            strToPrint = strToPrint.append(String.valueOf(count)).append(" ").append("0").append(" ").append(doc.get("DOCNO")).append(" ").append(String.valueOf(i + 1)).append(" ").append(String.valueOf(hits[i].score)).append(" ").append(fileName).append("\n");
        }
        fileWriter.write(strToPrint.toString());
    }

    public void closeResources() {
        try {
            this.fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
