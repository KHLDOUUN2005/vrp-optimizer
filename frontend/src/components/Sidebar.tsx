export default function Sidebar({
    stops, provider, setProvider, optimizer, setOptimizer, tourMode, setTourMode,
    handleOptimize, handleClear, handleLogout, loading, error, result
}: {
    stops: { lat: number, lng: number }[],
    provider: string, setProvider: (value: string) => void,
    optimizer: string, setOptimizer: (value: string) => void,
    tourMode: string, setTourMode: (value: string) => void,
    handleOptimize: () => void,
    handleClear: () => void,
    handleLogout: () => void,
    loading: boolean,
    error: string,
    result: any
}) {
    const selectClass = "w-full border border-gray-300 rounded-md p-2 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 appearance-none pr-8 cursor-pointer transition-colors duration-200 mb-3";
    
    const canUseBruteForce = stops.length <= 9;

    return (
        <div className="absolute top-4 left-4 z-[1000] bg-white p-4 rounded-lg shadow-lg w-80 h-[90%] flex flex-col">
            
            <div className="flex justify-between items-center mb-4 border-b pb-2">
                <h2 className="text-xl font-bold text-gray-800">Route Planner</h2>
                <button onClick={handleLogout} className="text-sm text-red-600 hover:text-red-800 font-semibold transition">
                    Logout
                </button>
            </div>

            <div className="overflow-y-auto pr-1 flex-grow">
                
                {error && (
                    <div className="bg-red-50 text-red-600 p-2 rounded text-sm mb-3 text-center">
                        {error}
                    </div>
                )}

                <label className="block text-sm font-semibold text-gray-700 mb-1">Provider</label>
                <select value={provider} onChange={(e) => setProvider(e.target.value)} className={selectClass}>
                    <option value="osrm">OSRM Provider</option>
                    <option value="haversine">Haversine Provider</option>
                </select>

                <label className="block text-sm font-semibold text-gray-700 mb-1">Optimizer</label>
                <select value={optimizer} onChange={(e) => setOptimizer(e.target.value)} className={selectClass}>
                    <option value="brute-force" disabled={!canUseBruteForce}>
                        Brute-Force {!canUseBruteForce && "(Max 9 stops)"}
                    </option>
                    <option value="nearest-neighbor">Nearest-Neighbor</option>
                    <option value="two-opt">Two-Opt</option>
                </select>

                <label className="block text-sm font-semibold text-gray-700 mb-1">Tour Mode</label>
                <select value={tourMode} onChange={(e) => setTourMode(e.target.value)} className={selectClass}>
                    <option value="CLOSED">CLOSED (Return to start)</option>
                    <option value="OPEN">OPEN (End at last stop)</option>
                </select>

                {result && (
                    <div className="bg-blue-50 border border-blue-200 p-3 rounded-md mb-4 mt-2">
                        <h3 className="font-bold text-blue-800 mb-2">Results</h3>
                        <div className="text-sm text-gray-700 flex flex-col gap-1">
                            <p><span className="font-semibold">Optimized:</span> {result.optimizedDistance.toFixed(2)} km</p>
                            <p><span className="font-semibold">Naive:</span> {result.naiveDistance.toFixed(2)} km</p>
                            <p className="text-green-600 font-bold"><span className="font-semibold">Saved:</span> {result.savingsPercent.toFixed(1)}%</p>
                            <p className="text-gray-500 text-xs mt-1">Time: {result.elapsedMs} ms | Mode: {result.mode}</p>
                        </div>
                    </div>
                )}

                <div className="mt-4">
                    <p className="text-sm font-semibold text-gray-700 mb-2">Stops Added: {stops.length}</p>
                    <ul className="mb-4">
                        {stops.map((stop, index) => (
                            <li key={index} className="text-sm border-b py-1 flex justify-between">
                                <span className="font-medium text-gray-600">Stop {index}</span>
                                <span className="text-gray-500">{stop.lat.toFixed(4)}, {stop.lng.toFixed(4)}</span>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>


            <div className="mt-4 pt-3 border-t">
                <button 
                    onClick={handleOptimize} 
                    disabled={loading || stops.length < 2}
                    className="w-full bg-blue-600 text-white font-bold py-2 rounded-md hover:bg-blue-700 transition disabled:bg-gray-400 mb-2"
                >
                    {loading ? "Optimizing..." : "Optimize Route"}
                </button>
                <button 
                    onClick={handleClear}
                    className="w-full bg-gray-200 text-gray-800 font-semibold py-2 rounded-md hover:bg-gray-300 transition"
                >
                    Clear Map
                </button>
            </div>

        </div>
    );
}