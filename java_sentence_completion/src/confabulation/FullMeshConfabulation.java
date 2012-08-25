package confabulation;

import java.io.IOException;

import utils.ArrayTools;

public class FullMeshConfabulation extends ConfabulationStub {

	public FullMeshConfabulation(int n_modules, String filename)
			throws IOException {

		// full mesh: a link between any two distinct modules
		boolean[][] kbs_spec = new boolean[n_modules][n_modules];
		for (int i = 0; i < kbs_spec.length; i++) {
			for (int j = 0; j < kbs_spec[i].length; j++) {
				if (i != j) {
					kbs_spec[i][j] = true;
				}
			}
		}
		init(kbs_spec, filename);
	}

	@Override
	public String check_argument(String[] symbols, int index_to_complete) {
		if (symbols == null) {
			return "symbols is null";
		}

		int index;
		if (index_to_complete < 0) {
			index = auto_index_to_complete(symbols);
			if (index < 0) {
				return "unable to find a suitable index to complete";
			}
		} else if (index_to_complete >= mods.length) {
			return "index to complete (" + index_to_complete
					+ ") is too big (max " + mods.length;
		} else {
			index = index_to_complete;
		}

		if (index < symbols.length && symbols[index] != null) {
			return "the index " + index
					+ "can't be completed because it is already set to "
					+ symbols[index];
		}
		return check_vocabulary(symbols);
	}

	@Override
	protected int auto_index_to_complete(String[] symbols) {
		int naive = naive_auto_index_to_complete(symbols);
		if (naive >= mods.length) {
			return -1;
		}
		return naive;
	}

	protected int naive_auto_index_to_complete(String[] symbols) {
		int first_null = ArrayTools.find_equal(null, symbols);
		return first_null >= 0 ? first_null : symbols.length;
	}

}
