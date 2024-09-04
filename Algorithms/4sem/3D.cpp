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
vector<bool> used;

vector<edge> e;
vector<vector<int>> g;

void add_edges(int u, int v, int c){
    g[u].push_back(e.size());
    e.push_back({u, v, c, 0});

    g[v].push_back(e.size());
    e.push_back({v, u, 0, 0});
}

bool bfs(int s, int t) {
    for (int& x : d) x = -1;

    d[s] = 0;
    int q[101];
    int head = 0, tail = 0;
    q[tail++] = s;

    while (head < tail) {
        int v = q[head++];

        for (int id: g[v]) {
            int u = e[id].v;
            if (d[u] == -1 && e[id].f < e[id].c) {
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

long long flow(int s, int t) {
    long long ans = 0;
    while (bfs(s, t)){
        for (int& x : level) x = 0;
        while (int a = dfs(s, INT_MAX, t)) {
            ans += a;
        }
    }
    return ans;
}

void dfs2(int v) {
    used[v] = true;
    for (int id : g[v]) {
        int u = e[id].v;
        if (!used[u]) {
            if (e[id].c - e[id].f > 0) {
                dfs2(u);
            }
        }
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n, m;
    cin >> n >> m;
    d.resize(n);
    g.resize(n);
    used.resize(n);
    level.resize(n);

    for (int i = 0; i < m; i++) {
        int u, v, c;
        cin >> u >> v >> c;
        u--; v--;
        add_edges(u, v, c);
        add_edges(v, u, c);
    }

    long long f = flow(0, n - 1);

    dfs2(0);
    vector<int> res;
    for (int i = 0; i < e.size(); i += 4) {
        int u = e[i].u;
        int v = e[i].v;
        if (used[u] && !used[v] || !used[u] && used[v]) {
            res.push_back(i / 4 + 1);
        }
    }

    cout << res.size() << " " << f << endl;
    for (int ee : res) {
        cout << ee << " ";
    }
    cout << endl;

    return 0;
}