package cz.cvut.kbss.owl2query;

import java.util.Arrays;
import java.util.Collection;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import cz.cvut.kbss.owl2query.QueryTester.ReasonerPlugin;

public class LUBM {
	public static void main(String[] args) {
		final QueryTester q = new QueryTester();
		// Collection<Integer> queries = Arrays.asList(1, /* ,2 */3, 4, 5, 6, 7,
		// /*
		// * ,8
		// * ,
		// * 9
		// */
		// 10 /* ,11, 12, 13 */, 14, 15, 16);
		Collection<Integer> queries = Arrays.asList(// 9
				 1, 2, 3, 4, 5, 6, 7, 8, 9,
				//
				// 10, 11,
				//
				// 12, 13, 14// ,
				// , 15, 16
				36

//				15, 
//				16,
//		 34
				// ,
//				 ,
				// ,
				// 37
				);
		// Collection<Integer> queries = Arrays.asList(12);

		final Collection<ReasonerPlugin> rs = Arrays.asList(
//		 (ReasonerPlugin) QueryTester.);
				// (ReasonerPlugin) QueryTester.kaon2);
				(ReasonerPlugin) QueryTester.getGenericOWLAPIv3(
				 TestConfiguration.FACTORY));
//						 new PelletReasonerFactory()));
						// (ReasonerPlugin) QueryTester
						// .getGenericOWLAPIv3(new ReasonerFactory()));
//						new FaCTPlusPlusReasonerFactory()));// new
		// PelletReasonerFactory()));//
		// new
		// StructuralReasonerFactory()));

		System.out.println("QUERY \t\t|\t1\t|\t2\t|\t3\t|\t# of results\t");
		System.out
				.println("==============================================================================");

		for (int i : queries) {
			for (final ReasonerPlugin<?> rp : rs) {

				System.out.print("\n" + i + "-" + rp.getAbbr() + "\t|\t");
				q
						.run(
								rp,
								"file:///home/kremen/work/java/datasets/lubm/query-complex/query"
										+ i + ".sparql",
								"/home/kremen/work/java/datasets/lubm/mapping",
								"file:///home/kremen/work/java/datasets/lubm/univ-bench.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-0.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-1.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-2.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-3.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-4.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-5.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-6.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-7.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-8.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-9.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-10.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-11.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-12.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-13.owl",
								"file:///home/kremen/work/java/datasets/lubm/university0-14.owl");
			}
			System.out
					.println("\n---------------------------------------------------------------------------------");
		}
	}
}
