#pragma GCC optimize("O2")

#include <set>
#include <vector>
#include <climits>
#include <iostream>

using namespace std;

struct edge {
    int u, v, f, c, w;

    edge(int u, int v, int c, int w) : u(u), v(v), f(0), c(c), w(w) {}
};

vector<int> d;
vector<int> path;
vector<edge> edges;
vector<vector<int>> g;

int dijkstra(int s, int t) {
    set<pair<int, int>> ss;
    ss.insert({s, 0});
    for (int & x : d) x = INT_MAX;
    for (int & x : path) x = INT_MAX;

    d[s] = 0;
    while (!ss.empty()) {
        auto [dist, u] = *(ss.begin());
        ss.erase({dist, u});
        if (dist > d[u]) continue;
        for (auto e : g[u]) {
            auto [_, v, f, c, w] = edges[e];
            if (c - f <= 0) continue;
            if (d[v] > d[u] + w) {
                d[v] = d[u] + w;
                path[v] = e;
                ss.insert({d[v], v});
            }
        }
    }

    return d[t] != INT_MAX;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n, m;
    cin >> n >> m;

    const int s = 0;
    const int t = n - 1;

    g.resize(n);
    d.resize(n);
    path.resize(n);

    for (int i = 0; i < m; i++) {
        int u, v, c, w;
        cin >> u >> v >> c >> w;
        u--, v--;
        g[u].push_back((int) edges.size());
        edges.emplace_back(u, v, c, w);
        g[v].push_back((int) edges.size());
        edges.emplace_back(v, u, 0, -w);
    }

    long long min_cost_max_flow = 0;

    while (dijkstra(s, t)) {
        int v = t;
        int flow = INT_MAX;
        while (v != s) {
            auto [u, _, f, c, w] = edges[path[v]];
            flow = min(flow, c - f);
            v = u;
        }
        for (v = t; v != s; v = edges[path[v]].u) {
            auto e = path[v];
            edges[e].f += flow;
            edges[e + (e & 1 ? -1 : 1)].f -= flow;
            min_cost_max_flow += (long long) flow * edges[e].w;
        }
    }

    cout << min_cost_max_flow << endl;

    return 0;
}