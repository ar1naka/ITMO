#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

vector<vector<int>> graph;
vector<int> used, d, up;
vector<int> ans;
int c = 0;

void dfs(int v, int p = -1){
    used[v] = true;
    d[v] = up[v] = c++;
    int a = 0;
    for (int i = 0; i < graph[v].size(); i++){
        int to = graph[v][i];
        if (to == p) continue;
        if (used[to]){
            up[v] = min(up[v], d[to]);
        } else {
            dfs(to, v);
            up[v] = min(up[v], up[to]);
            if ((up[to] >= d[v]) && (p != -1)){
                ans[v] = 1;
            }
            a++;
        }
    }
    if (a > 1 && p == -1){
        ans[v] = 1;
    }
}

int main(){
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    int n, m;
    cin >> n >> m;

    graph.resize(n + m + 1);
    used.resize(n + m + 1);
    d.resize(n + m + 1);
    up.resize(n + m + 1);
    ans.resize(n + m + 1);

    for (int i = 0; i < m; i++) {
        int a, b, c;
        cin >> a >> b >> c;
        graph[n + i + 1].push_back(a);
        graph[a].push_back(n + i + 1);
        graph[n + i + 1].push_back(b);
        graph[b].push_back(n + i + 1);
        graph[n + i + 1].push_back(c);
        graph[c].push_back(n + i + 1);
    }

    for (int i = 1; i <= n; i++){
        if (!used[i]){
            dfs(i);
        }
    }

    int cnt = 0;
    for (int i = n + 1; i <= n + m; i++){
        if (ans[i]) cnt++;
    }
    cout << cnt << endl;

    if (cnt > 0){
        for (int i = n + 1; i <= n + m; i++){
            if (ans[i]) cout << i - n << " ";
        }
        cout << endl;
    }
    return 0;
}