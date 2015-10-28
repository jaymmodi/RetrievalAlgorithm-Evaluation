package Assignment2;


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
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class CompareAlgorithms {


    public static void main(String[] args) {
        String queryString = "Airbus Subsidies";

        String indexPath = "D:\\Jay\\IUB\\Fall_2015\\Search\\Programming Assignment\\Assignment -2\\index";

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            searcher.setSimilarity(new BM25Similarity());


            QueryParser parser = new QueryParser("TEXT", analyzer);
            Query query = parser.parse(QueryParser.escape(queryString));
            System.out.println("Searching for : " + query.toString("TEXT"));

            TopDocs results = searcher.search(query, 1000);

            int numTotalHits = results.totalHits;
            System.out.println(numTotalHits + "total matching documents");

            ScoreDoc[] hits = results.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                Document doc = searcher.doc(hits[i].doc);
                System.out.println(doc.get("DOCNO"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
