#pragma GCC optimize("Ofast")
#include <bits/stdc++.h>


#include <ext/pb_ds/assoc_container.hpp>
using namespace __gnu_pbds;
using namespace std;

typedef vector<int> vi;
typedef vector<vi> vvi;
typedef pair<int, int> ii;
typedef vector<ii> vii;
typedef long long ll;
typedef vector<ll> vll;
typedef vector<vll> vvll;
typedef long double ld;

typedef tree<int,null_type,less<int>,rb_tree_tag,
tree_order_statistics_node_update> indexed_set;

#define endll '\n'
#define all(x) (x).begin(), (x).end()
#define rall(x) (x).rbegin, (x).rend()

#define present(c,x) ((c).find(x) != (c).end())
#define cpresent(c,x) (find(all(c),x) != (c).end())

#define sz(a) ll((a).size())
#define MOD ll(1e9+7)
#define INF ll(2e63-1)


inline void open(const char *fin, const char *fout)
{
    freopen(fin, "r", stdin);
    freopen(fout, "w", stdout);
}


int main()
{
    ios::sync_with_stdio(false);
    cin.tie(0); cout.tie(0);

    int T; cin >> T;
    for(int t = 0; t < T; t++)
    {
        int N; cin >> N;
        vector<int> arr(N);

        for(int i = 0; i < N; i++) cin >> arr[i];
        
        int rMin = 1e9, ans = 0;
        for(int i = N - 1; i >= 0; i--)
        {
            if(arr[i] < rMin) rMin = arr[i];

            if(arr[i] > rMin) ans++;
        }
        
        cout << ans << endll;
    }
}