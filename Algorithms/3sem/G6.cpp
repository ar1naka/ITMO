#pragma GCC optimize("O3")

#include <vector>
#include <climits>
#include <iostream>

using namespace std;

int main() {
    int n, m, k, s;
    cin >> n >> m >> k >> s;

    vector<vector<pair<int, int>>> g;
    g.resize(n, vector<pair<int, int>>());

    for (size_t i = 0; i < m; i++) {
        int u, v, w;
        cin >> u >> v >> w;
        g[u - 1].emplace_back(v - 1, w);
    }

    vector<vector<int>> dist;
    dist.resize(n, vector(k + 1, INT_MAX));

    dist[s - 1][0] = 0;
    for (size_t i = 1; i <= k; i++) {
        for (size_t u = 0; u < n; u++) {
            for (pair<int, int> e : g[u]) {
                if (dist[u][i - 1] != INT_MAX) {
                    dist[e.first][i] = min(dist[e.first][i], dist[u][i - 1] + e.second);
                }
            }
        }
    }

    for (size_t i = 0; i < n; i++) {
        cout << (dist[i][k] == INT_MAX ? -1 : dist[i][k]) << endl;
    }

    return 0;
}