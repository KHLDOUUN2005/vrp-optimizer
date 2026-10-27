import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapContainer, TileLayer, CircleMarker, Polyline, useMapEvents, ZoomControl, Tooltip } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import Sidebar from '../components/Sidebar';
import L from 'leaflet';
import 'leaflet-polylinedecorator';
import { useMap } from 'react-leaflet';

function RouteArrows({ positions }: { positions: [number, number][] }) {
    const map = useMap();
    
    useEffect(() => {
        if (!positions || positions.length < 2) return;
        
        
        const polyline = L.polyline(positions);
        
      
        const decorator = L.polylineDecorator(polyline, {
            patterns: [
                {
                    offset: 25,          
                    repeat: 100,         
                    symbol: L.Symbol.arrowHead({
                        pixelSize: 12,
                        polygon: false,
                        pathOptions: { stroke: true, color: '#10b981', weight: 3 }
                    })
                }
            ]
        }).addTo(map);

        return () => {
            decorator.remove();
        };
    }, [map, positions]);

    return null;
}

function MapClicker({ onAddStop }: { onAddStop: (lat: number, lng: number) => void }) {
    useMapEvents({
        click(e) {
            const { lat, lng } = e.latlng;
         
            if (lat < 33.4 || lat > 33.8 || lng < -7.8 || lng > -7.3) {
                alert("Please click within the Casablanca region.");
                return;
            }
            onAddStop(lat, lng);
        }
    });
    return null;
}

export default function Map() {
    const navigate = useNavigate();
    
    const [stops, setStops] = useState<{ lat: number; lng: number }[]>([]);
    
    
    const [provider, setProvider] = useState('osrm');
    const [optimizer, setOptimizer] = useState('two-opt');
    const [tourMode, setTourMode] = useState('CLOSED');
    
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [result, setResult] = useState<any>(null); 

    
    useEffect(() => {
        if (!localStorage.getItem("token")) {
            navigate("/login");
        }
    }, [navigate]);

    const handleAddStop = (lat: number, lng: number) => {
        setStops([...stops, { lat, lng }]);
    };

    const handleClear = () => {
        setStops([]);
        setResult(null);
        setError('');
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    };

    const handleOptimize = async () => {
        if (stops.length < 2) {
            setError("Please add at least 2 stops.");
            return;
        }

        setError('');
        setLoading(true);
        setResult(null);

        const stopsToSend = stops.map((stop, index) => ({
            id: index, 
            label: `Stop ${index}`, 
            latitude: stop.lat, 
            longitude: stop.lng
        }));

        const token = localStorage.getItem("token");

        try {
            const response = await fetch("http://localhost:8080/api/optimize", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json', 
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ 
                    stops: stopsToSend, 
                    depotIndex: 0, 
                    mode: tourMode, 
                    provider: provider, 
                    optimizer: optimizer 
                })
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                const msg = errorData?.message || "Optimization failed. Please try again.";
                throw new Error(msg);
            }

            const data = await response.json();
            setResult(data);
        } catch (err) {
            if (err instanceof Error) setError(err.message);
            else setError("An unexpected error occurred.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="h-screen w-full relative">
            
            <Sidebar
                stops={stops}
                provider={provider}
                setProvider={setProvider}
                optimizer={optimizer}
                setOptimizer={setOptimizer}
                tourMode={tourMode}
                setTourMode={setTourMode}
                handleOptimize={handleOptimize}
                handleClear={handleClear}
                handleLogout={handleLogout}
                loading={loading}
                error={error}
                result={result}
            />

            <MapContainer
                center={[33.5731, -7.5898]}
                zoom={12}
                zoomControl={false}
                className='h-screen w-full z-0'
            >
                <TileLayer url='https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png' />
                <MapClicker onAddStop={handleAddStop} />
                <ZoomControl position="topright" />

                
                {stops.map((stop, index) => {
                    const isDepot = index === 0; 
                    return (
                        <CircleMarker
                            key={index}
                            center={[stop.lat, stop.lng]}
                            radius={isDepot ? 18 : 12}
                            pathOptions={
                                isDepot 
                                ? { color: '#10b981', fillColor: '#10b981', fillOpacity: 1 } 
                                : { color: 'blue', fillColor: 'blue', fillOpacity: 0.8 }   
                            }
                        >
                            <Tooltip permanent direction="center" className="bg-transparent border-0 text-white font-bold text-sm shadow-none">
                                {isDepot ? "D" : index} 
                            </Tooltip>
                        </CircleMarker>
                    );
                })}

                
                {result && (
                    <>
                        <Polyline
                            positions={result.geometry}
                            color="#ef4444"
                            weight={6} 
                            opacity={0.8}
                        />
                        <RouteArrows positions={result.geometry} />
                    </>
                )}
            </MapContainer>
        </div>
    );
}