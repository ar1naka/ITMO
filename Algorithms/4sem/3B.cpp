#pragma GCC optimize("O2")

#include <vector>
#include <climits>
#include <iostream>

using namespace std;

struct edge {
    int u, v;
    int c = 0, f = 0;
};

vector<int> d;
vector<int> level;

vector<edge> e;
vector<vector<int>> g;

void add_edges(int u, int v, int c){
    g[u].push_back(e.size());
    e.push_back({u, v, c, 0});

    g[v].push_back(e.size());
    e.push_back({v, u, 0, 0});
}

bool bfs(int s, int t, int max_c) {
    fill(d.begin(), d.end(), -1);

    d[s] = 0;
    int q[501];
    int head = 0, tail = 0;
    q[tail++] = s;

    while (head < tail) {
        int v = q[head++];

        for (int id: g[v]) {
            int u = e[id].v;
            if (d[u] == -1 && e[id].f + max_c <= e[id].c) {
                d[u] = d[v] + 1;
                q[tail++] = u;
            }
        }
    }

    return d[t] != -1;
}

int dfs(int v, int f, int t) {
    if (v == t || f == 0) {
        return f;
    }

    for (int& cid = level[v]; cid < g[v].size(); cid++) {
        int id = g[v][level[v]];
        int u = e[id].v;
        if (d[u] != d[v] + 1) {
            continue;
        }
        int tr = dfs(u, min(f, e[id].c - e[id].f), t);
        if (!tr) {
            continue;
        }
        e[id].f += tr;
        e[id^1].f -= tr;
        return tr;

    }

    return 0;
}

long long flow(int s, int t, int max_c) {
    long long ans = 0;
    while (max_c) {
        while (bfs(s, t, max_c)) {
            fill(level.begin(), level.end(), 0);
            while (int a = dfs(s, INT_MAX, t)) {
                ans += a;
            }
        }
        max_c /= 2;
    }
    return ans;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n, m;
    cin >> n >> m;
    d.resize(n);
    g.resize(n);
    level.resize(n);

    int max_c = 0;
    for (int i = 0; i < m; i++) {
        int u, v, c;
        cin >> u >> v >> c;
        u--; v--;
        max_c = max(max_c, c);
        add_edges(u, v, c);
        add_edges(v, u, 0);
    }

    cout << flow(0, n - 1, max_c) << endl;


    return 0;
}