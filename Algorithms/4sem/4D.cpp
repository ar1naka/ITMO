#pragma GCC optimize("O2")

#include <set>
#include <string>
#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

struct edge {
    int u, v, f, c, w;

    edge(int u, int v, int c, int w) : u(u), v(v), f(0), c(c), w(w) {}
};

vector<int> d;
vector<int> path;
vector<edge> edges;
vector<vector<int>> g;

int encode(char ch) {
    return ch >= 'a' ? ch - 'a' + 1 : ch - 'A' + 27;
}

char decode(int x) {
    return x <= 26 ? 'a' + x - 1 : 'A' + x - 27;
}

void add_edges(int u, int v, int c, int w) {
    g[u].push_back((int) edges.size());
    edges.emplace_back(u, v, c, w);
    g[v].push_back((int) edges.size());
    edges.emplace_back(v, u, 0, -w);
}

int dijkstra(int s, int t) {
    set<pair<int, int>> ss;
    ss.insert({0, s});
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

    int n, k;
    cin >> n >> k;

    const int MAX = 1 << 13;
    g.resize(MAX);
    d.resize(MAX);
    path.resize(MAX);

    string code;
    cin >> code;

    string answer;
    cin >> answer;

    const int s = 2 * k + 1;
    const int t = 2 * k + 2;

    int count[56][56];
    for (auto & sub : count) {
        for (int & x : sub) x = 0;
    }
    for(int i = 0; i < n; i++) {
        count[encode(code[i])][encode(answer[i])]++;
    }

    int id[56][56];
    for (auto & sub : id) {
        for (int & x : sub) x = 0;
    }
    for (int i = 1, l = 0; i <= k; i++) {
        add_edges(s, i, 1, 0);
        l += 2;
        for (int j = 1; j <= k; j++) {
            id[i][j] = l;
            add_edges(i, j + k, 1, -count[i][j]);
            l += 2;
        }
        add_edges(i + k, t, 1, 0);
        l += 2;
    }

    /* for (int i = 0; i < k; i++) {
        add_edges(s, i, 1, 0);
        add_edges(i + k, t, 1, 0);
    } */

    ll result = 0;
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
            result += (long long) flow * edges[e].w;
        }
    }

    cout << -result << endl;

    char dec[k];
    for (int i = 0; i < k; i++) {
        for (int j = 1; j <= k; j++) {
            if (edges[id[i + 1][j]].c == edges[id[i + 1][j]].f) {
                dec[i] = decode(j);
                break;
            }
        }
    }

    cout << string(dec) << endl;

    return 0;
}