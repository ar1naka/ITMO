#pragma GCC optimize("O2")

#include <queue>
#include <vector>
#include <iostream>

using namespace std;

typedef long long ll;

struct edge {
    int u, v;
};

vector<vector<int>> g;
vector<int> b, p, used, match, bloss;

void clear() {
    for (auto& x : p) x = -1;
    for (auto& x : used) x = false;
    for (int i = 0; i < b.size(); i++) b[i] = i;
}

bool is_blossom(int u, int v) {
    return u == v || match[v] != -1 && p[match[v]] != -1;
}

void mark(int u, int v, int c) {
    while (b[u] != v) {
        bloss[b[u]] = true;
        bloss[b[match[u]]] = true;
        p[u] = c;
        c = match[u];
        u = p[match[u]];
    }
}

int lca(int u, int v) {
    vector<bool> vis(p.size(), false);

    while (true) {
        u = b[u];
        vis[u] = true;
        if (match[u] == -1) {
            break;
        }
        u = p[match[u]];
    }

    while (true) {
        v = b[v];
        if (vis[v]) {
            return v;
        }
        v = p[match[v]];
    }
}

int path(int u) {
    clear();
    used[u] = true;

    queue<int> q;
    q.push(u);
    while (!q.empty()) {
        int v = q.front(); q.pop();
        for (int i = 0; i < g[v].size(); i++) {
            int to = g[v][i];
            if (b[v] == b[to]) {
                continue;
            }
            if (to == match[v]) {
                continue;
            }

            if (is_blossom(u, to)) {
                int c = lca(v, to);
                for (auto& x : bloss) x = false;
                mark(v, c, to);
                mark(to, c, v);
                for (int j = 0; j < p.size(); j++) {
                    if (bloss[b[j]]) {
                        b[j] = c;
                        if (!used[j]) {
                            q.push(j);
                            used[j] = true;
                        }
                    }
                }
            } else if (p[to] == -1) {
                p[to] = v;
                if (match[to] == -1) {
                    return to;
                }
                q.push(match[to]);
                used[match[to]] = true;
            }
        }
    }

    return -1;
}

void fill_result(vector<edge>& edges) {
    for (int i = 0; i < match.size(); i++) {
        if (match[i] != -1) {
            int u = i;
            int v = match[i];
            match[u] = -1;
            match[v] = -1;
            edges.push_back({++u, ++v});
        }
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n, m;
    cin >> n >> m;

    b.resize(n);
    p.resize(n);
    g.resize(n);
    used.resize(n);
    bloss.resize(n);
    match.resize(n, -1);

    for (int i = 0; i < m; i++) {
        int u, v;
        cin >> u >> v;
        u--, v--;
        g[u].push_back(v);
        g[v].push_back(u);
    }

    for (int u = 0; u < n; u++) {
        if (match[u] == -1) {
            for (int v = path(u); v != -1; ) {
                int tmp = match[p[v]];
                match[v] = p[v], match[p[v]] = v;
                v = tmp;
            }
        }
    }

    vector<edge> edges;
    fill_result(edges);

    cout << edges.size() << '\n';
    for (auto e : edges) {
        cout << e.u << " " << e.v << '\n';
    }

    return 0;
}