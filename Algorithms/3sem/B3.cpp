#include <iostream>
#include <vector>
#include <set>
#include <map>
#include <algorithm>
#include <stack>


struct edge {
    int a;
    int b;
};
using namespace std;
vector<vector<int>> graph;
vector<int> used, d, up;
set<int> ans;
int c = 0;

void dfs(int v, int p = -1){
    used[v] = true;
    d[v] = up[v] = c++;
    int a = 0;
    for (int i = 0; i < graph[v].size();i++){
        int to = graph[v][i];
        if (to == p) continue;
        if (used[to]){
            up[v] = min(up[v], d[to]);
        } else {
            dfs(to, v);
            up[v] = min(up[v], up[to]);
            if ((up[to] >= d[v]) && (p != -1)){
                ans.insert(v);
            }
            a++;
        }
    }
    if (a > 1 && p == -1){
        ans.insert(v);
    }
}
int main(){
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int n, m;
    scanf("%d %d", &n, &m);

    graph.resize(n);
    used.resize(n);
    d.resize(n);
    up.resize(n);
    for (int i = 0; i < m; i++) {
        int a, b;
        scanf("%d %d", &a, &b);
        graph[a - 1].push_back(b - 1);
        graph[b - 1].push_back(a - 1);
    }

    for (int i = 0; i < n; i++){
        if (!used[i]){
            dfs(i);
        }
    }
    cout << ans.size() << endl;
    for (int x : ans){
        cout << x + 1 << " ";
    }
}