#include <iostream>
#include <vector>
#include "algorithm"
#pragma GCC optimize("Ofast")

using  namespace std;
int n, m;
vector<vector<int>> res;
int color[100001];
vector<int> cycle;
int cycle_st, cycle_end;

bool dfs(int v) {
    color[v] = 1;
    for (int u : res[v]) {
        if (color[u] == 1) {
            cycle_st = u;
            cycle_end = v;
            return true;
        }else if (color[u] == 0) {
            cycle[u] = v;
            if (dfs(u)) return true;
        }
    }
    color[v] = 2;
    return false;
}


int main(){
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
    int x, y;
    cin >> n >> m;
    res.resize(n, vector<int>());
    cycle.resize(n,-1);
    for (int i = 0; i < m; i++) {
        cin >> x >> y;
        res[x - 1].push_back(y - 1);
    }
    bool flag = false;
    for (int i = 0; i < n; i++) {
        if (dfs(i)) {
            flag = true;
            break;
        }
    }
    if (flag){
        vector<int> ans;
        ans.push_back(cycle_st);
        int ind = cycle_end;
        while (ind != cycle_st) {
            ans.push_back(ind);
            ind = cycle[ind];
        }
        std::reverse(ans.begin(), ans.end());
        cout << ans.size() << endl;
        for (auto c : ans) {
            cout << c + 1 << ' ';
        }
    } else {
        cout << "-1" << endl;
    }
    return 0;
}
 